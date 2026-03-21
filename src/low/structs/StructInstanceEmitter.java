package low.structs;

import ast.ASTNode;
import ast.structs.StructInstanceNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.functions.TypeMapper;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMString;
import low.module.builders.structs.LLVMStruct;

import java.util.List;
import java.util.Map;


public class StructInstanceEmitter {

    private final TempManager tempManager;
    private final GlobalStringManager stringManager;

    public StructInstanceEmitter(TempManager tempManager, GlobalStringManager stringManager) {
        this.tempManager = tempManager;
        this.stringManager = stringManager;
    }

    public LLVMValue emit(StructInstanceNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        // ===== RESOLVER TIPO REAL DA STRUCT =====
        Type instanceType = node.getType();
        if (!(instanceType instanceof StructType structType)) {
            throw new RuntimeException("StructInstanceNode sem StructType resolvido: " + instanceType);
        }

        String structName = structType.name();
        LLVMStruct structLLVMType = new LLVMStruct(structName);
        LLVMPointer structPtrType = new LLVMPointer(structLLVMType); // ponteiro para struct

        // ===== RESOLVER DEFINIÇÃO =====
        StructNode def = visitor.getStructNode(structName);
        System.out.println("debug for struct instance " + def.getName());
        if (def == null) {
            throw new RuntimeException("Struct não encontrada: " + structName);
        }

        // ===== malloc =====
        int structSize = def.getLLVMSizeBytes();
        String mallocTmp = tempManager.newTemp();
        llvm.append("  ").append(mallocTmp)
                .append(" = call i8* @malloc(i64 ").append(structSize).append(")\n");

        String structPtrTmp = tempManager.newTemp();
        llvm.append("  ").append(structPtrTmp)
                .append(" = bitcast i8* ").append(mallocTmp)
                .append(" to ").append(structPtrType).append("\n");

        // ===== CAMPOS =====
        List<VariableDeclarationNode> fields = def.getFields();
        List<ASTNode> posValues = node.getPositionalValues();
        Map<String, ASTNode> namedValues = node.getNamedValues();
        int providedPos = posValues != null ? posValues.size() : 0;

        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            Type fieldType = field.getType();
            ASTNode providedValue = null;

            if (namedValues != null && namedValues.containsKey(field.getName())) {
                providedValue = namedValues.get(field.getName());
            } else if (posValues != null && i < providedPos) {
                providedValue = posValues.get(i);
            }

            LLVMValue fieldValue;

            if (providedValue != null) {
                fieldValue = providedValue.accept(visitor); // retorna LLVMValue
                llvm.append(fieldValue.getCode());
            } else {
                fieldValue = defaultValue(fieldType, llvm);
            }

            // Ponteiro para o campo
            String fieldPtr = tempManager.newTemp();
            LLVMPointer fieldPtrType = new LLVMPointer(fieldValue.getType()); // ponteiro para tipo do campo
            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVMType).append(", ")
                    .append(structPtrType).append(" ").append(structPtrTmp)
                    .append(", i32 0, i32 ").append(i).append("\n");

            // Store do valor no campo
            llvm.append("  store ")
                    .append(fieldValue.getType())
                    .append(" ").append(fieldValue.getName())
                    .append(", ").append(fieldPtrType)
                    .append(" ").append(fieldPtr)
                    .append("\n");
        }

        return new LLVMValue(structPtrType, structPtrTmp, llvm.toString());
    }

    private LLVMValue defaultValue(Type type, StringBuilder llvm) {
        if (type instanceof PrimitiveTypes prim) {
            LLVMTYPES llvmType = TypeMapper.from(prim);
            String tmp = tempManager.newTemp();

            switch (prim.name()) {
                case "int", "boolean" -> {
                    return new LLVMValue(llvmType, "0", "");
                }
                case "double", "float" -> {
                    return new LLVMValue(llvmType, "0.0", "");
                }
                case "string" -> {
                    String emptyLabel = stringManager.getGlobalName("");
                    llvm.append("  ").append(tmp)
                            .append(" = call %String* @createString(i8* ").append(emptyLabel).append(")\n");
                    return new LLVMValue(new LLVMString(), tmp, "");
                }
                default -> throw new RuntimeException("Unsupported primitive: " + prim.name());
            }
        }

        if (type instanceof StructType st) {
            LLVMStruct structLLVM = new LLVMStruct(st.name());
            LLVMPointer ptrType = new LLVMPointer(structLLVM);
            return new LLVMValue(ptrType, "null", "");
        }

        if (type instanceof ListType listType) {
            Type elemType = listType.elementType();
            String tmp = tempManager.newTemp();
            LLVMTYPES llvmListType = TypeMapper.from(listType);

            if (elemType instanceof PrimitiveTypes prim) {
                switch (prim.name()) {
                    case "int" -> llvm.append("  ").append(tmp)
                            .append(" = call %struct.ArrayListInt* @arraylist_create_int(i64 10)\n");
                    case "double" -> llvm.append("  ").append(tmp)
                            .append(" = call %struct.ArrayListDouble* @arraylist_create_double(i64 10)\n");
                    case "boolean" -> llvm.append("  ").append(tmp)
                            .append(" = call %struct.ArrayListBool* @arraylist_create_bool(i64 10)\n");
                    default -> llvm.append("  ").append(tmp)
                            .append(" = call %ArrayList* @arraylist_create(i64 10)\n");
                }
            } else {
                llvm.append("  ").append(tmp)
                        .append(" = call %ArrayList* @arraylist_create(i64 10)\n");
            }

            return new LLVMValue(llvmListType, tmp, "");
        }

        throw new RuntimeException("Unsupported field type for default value: " + type);
    }
}