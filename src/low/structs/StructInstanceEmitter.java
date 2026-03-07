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

import java.util.List;
import java.util.Map;

public class StructInstanceEmitter {
    private final TempManager tempManager;
    private final GlobalStringManager stringManager;

    public StructInstanceEmitter(TempManager tempManager, GlobalStringManager stringManager) {
        this.tempManager = tempManager;
        this.stringManager = stringManager;
    }

    public String emit(StructInstanceNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        TypeMapper mapper = new TypeMapper();

        // ===== RESOLVER TIPO REAL DA STRUCT =====
        Type instanceType = node.getType();

        if (!(instanceType instanceof StructType structType)) {
            throw new RuntimeException("StructInstanceNode sem StructType resolvido: " + instanceType);
        }

        String structName = structType.name();
        String structLLVMType = "%" + structName;

        // ===== RESOLVER DEFINIÇÃO =====
        StructNode def = visitor.getStructNode(structName);

        if (def == null) {
            throw new RuntimeException("Struct não encontrada: " + structName);
        }

        // ===== TAMANHO =====
        int structSize = def.getLLVMSizeBytes();

        // ===== malloc =====
        String mallocTmp = tempManager.newTemp();
        llvm.append("  ").append(mallocTmp)
                .append(" = call i8* @malloc(i64 ").append(structSize).append(")\n");

        String structPtr = tempManager.newTemp();
        llvm.append("  ").append(structPtr)
                .append(" = bitcast i8* ").append(mallocTmp)
                .append(" to ").append(structLLVMType).append("*\n");

        // ===== CAMPOS =====
        List<VariableDeclarationNode> fields = def.getFields();
        List<ASTNode> posValues = node.getPositionalValues();
        Map<String, ASTNode> namedValues = node.getNamedValues();

        int providedPos = (posValues == null ? 0 : posValues.size());

        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            String fieldName = field.getName();
            Type fieldType = field.getType();

            ASTNode providedValue = null;

            if (namedValues != null && namedValues.containsKey(fieldName)) {
                providedValue = namedValues.get(fieldName);
            } else if (posValues != null && i < providedPos) {
                providedValue = posValues.get(i);
            }

            String fieldLLVMType = mapFieldTypeForStruct(fieldType);

            String valueTemp;
            String before = "";

            if (providedValue != null) {
                before = providedValue.accept(visitor);
                valueTemp = extractTemp(before);
            } else {
                valueTemp = emitDefaultValue(fieldType, fieldLLVMType, llvm);
            }

            llvm.append(before);

            String fieldPtr = tempManager.newTemp();
            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVMType).append(", ")
                    .append(structLLVMType).append("* ")
                    .append(structPtr)
                    .append(", i32 0, i32 ").append(i).append("\n");

            llvm.append("  store ")
                    .append(fieldLLVMType).append(" ")
                    .append(valueTemp)
                    .append(", ")
                    .append(fieldLLVMType).append("* ")
                    .append(fieldPtr).append("\n");
        }

        llvm.append(";;VAL:")
                .append(structPtr)
                .append(";;TYPE:")
                .append(structLLVMType)
                .append("*\n");

        return llvm.toString();
    }

    private String mapFieldTypeForStruct(Type type) {

        TypeMapper mapper = new TypeMapper();

        if (type instanceof ListType listType) {

            Type inner = listType.elementType();

            if (inner instanceof PrimitiveTypes prim) {
                return switch (prim.name()) {
                    case "int" -> "%struct.ArrayListInt*";
                    case "double" -> "%struct.ArrayListDouble*";
                    case "boolean" -> "%struct.ArrayListBool*";
                    case "string" -> "%ArrayList*";
                    default -> "%ArrayList*";
                };
            }

            return "%ArrayList*";
        }

        if (type instanceof StructType structType) {
            return "%" + structType.name() + "*";
        }

        if (type instanceof PrimitiveTypes prim) {
            return mapper.toLLVM(prim);
        }

        throw new RuntimeException("Unsupported field type: " + type);
    }

    private String emitDefaultValue(Type type, String fieldLLVMType, StringBuilder llvm) {

        if (type instanceof PrimitiveTypes prim) {
            return switch (prim.name()) {

                case "int" -> "0";
                case "double", "float" -> "0.0";
                case "boolean" -> "0";

                case "string" -> {
                    String emptyLabel = stringManager.getGlobalName("");
                    String tmp = tempManager.newTemp();
                    llvm.append("  ").append(tmp)
                            .append(" = call %String* @createString(i8* ")
                            .append(emptyLabel).append(")\n");
                    yield tmp;
                }

                default -> "zeroinitializer";
            };
        }

        if (type instanceof StructType) {
            return "null";
        }

        if (type instanceof ListType listType) {

            Type inner = listType.elementType();
            String tmp = tempManager.newTemp();

            if (inner instanceof PrimitiveTypes prim) {
                switch (prim.name()) {
                    case "int" -> {
                        llvm.append("  ").append(tmp)
                                .append(" = call %struct.ArrayListInt* @arraylist_create_int(i64 10)\n");
                        return tmp;
                    }
                    case "double" -> {
                        llvm.append("  ").append(tmp)
                                .append(" = call %struct.ArrayListDouble* @arraylist_create_double(i64 10)\n");
                        return tmp;
                    }
                    case "boolean" -> {
                        llvm.append("  ").append(tmp)
                                .append(" = call %struct.ArrayListBool* @arraylist_create_bool(i64 10)\n");
                        return tmp;
                    }
                }
            }

            llvm.append("  ").append(tmp)
                    .append(" = call %ArrayList* @arraylist_create(i64 10)\n");
            return tmp;
        }

        return "zeroinitializer";
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) {
            throw new RuntimeException("Cannot find ;;VAL: in: " + code);
        }
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
