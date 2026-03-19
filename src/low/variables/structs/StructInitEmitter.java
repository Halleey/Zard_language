package low.variables.structs;


import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMBool;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;
import low.module.builders.primitives.LLVMString;
import low.module.builders.structs.LLVMStruct;
import low.variables.VariableEmitter;

public class StructInitEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final VariableEmitter varEmitter;

    public StructInitEmitter(
            TempManager temps,
            LLVisitorMain visitor,
            VariableEmitter varEmitter
    ) {
        this.temps = temps;
        this.visitor = visitor;
        this.varEmitter = varEmitter;
    }

    private String getVarPtr(String name) {
        return varEmitter.getVarPtr(name);
    }

    public LLVMValue emit(VariableDeclarationNode node, TypeInfos info) {

        StringBuilder llvm = new StringBuilder();

        Type type = info.getType();
        if (!(type instanceof StructType structType)) {
            throw new RuntimeException("Esperado StructType, encontrado: " + type);
        }

        // %Row*
        String structName = structType.name();

        String varPtr = getVarPtr(node.getName());

        StructNode structDef = visitor.getStructNode(structName);
        if (structDef == null) {
            throw new RuntimeException("Struct não encontrada: " + structName);
        }

        String structLLVM = "%" + structName;

        String gepTmp  = temps.newTemp();
        String sizeTmp = temps.newTemp();
        String rawPtr  = temps.newTemp();
        String objPtr  = temps.newTemp();

        llvm.append("  ").append(gepTmp)
                .append(" = getelementptr ").append(structLLVM)
                .append(", ").append(structLLVM).append("* null, i32 1\n");

        llvm.append("  ").append(sizeTmp)
                .append(" = ptrtoint ").append(structLLVM)
                .append("* ").append(gepTmp).append(" to i64\n");

        llvm.append("  ").append(rawPtr)
                .append(" = call i8* @malloc(i64 ").append(sizeTmp).append(")\n");

        llvm.append("  ").append(objPtr)
                .append(" = bitcast i8* ").append(rawPtr)
                .append(" to ").append(structLLVM).append("*\n");

        var fields = structDef.getFields();

        for (int i = 0; i < fields.size(); i++) {
            VariableDeclarationNode field = fields.get(i);
            Type fieldType = field.getType();

            if (!(fieldType instanceof ListType listType)) continue;

            visitor.registerListElementType(node.getName(), listType.elementType());

            LLVMTYPES listTypeLLVM;
            String createFn;

            Type elemType = listType.elementType();

            if (elemType == PrimitiveTypes.INT) {
                listTypeLLVM = new LLVMArrayList(new LLVMInt());
                createFn = "@arraylist_create_int";
            } else if (elemType == PrimitiveTypes.DOUBLE) {
                listTypeLLVM = new LLVMArrayList(new LLVMDouble());
                createFn = "@arraylist_create_double";
            } else if (elemType == PrimitiveTypes.BOOL) {
                listTypeLLVM = new LLVMArrayList(new LLVMBool());
                createFn = "@arraylist_create_bool";
            } else if (elemType == PrimitiveTypes.STRING) {
                listTypeLLVM = new LLVMArrayList(new LLVMString());
                createFn = "@arraylist_string_create";
            } else {
                listTypeLLVM = new LLVMArrayList(null);
                createFn = "@arraylist_create";
            }

            String listTmp  = temps.newTemp();
            String fieldPtr = temps.newTemp();

            llvm.append("  ").append(listTmp)
                    .append(" = call ").append(listTypeLLVM)
                    .append(" ").append(createFn)
                    .append("(i64 10)\n");

            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVM).append(", ").append(structLLVM)
                    .append("* ").append(objPtr)
                    .append(", i32 0, i32 ").append(i).append("\n");

            llvm.append("  store ").append(listTypeLLVM)
                    .append(" ").append(listTmp)
                    .append(", ").append(listTypeLLVM)
                    .append("* ").append(fieldPtr).append("\n");
        }

        llvm.append("  store ").append(structLLVM).append("* ").append(objPtr)
                .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

        return new LLVMValue(new LLVMStruct(structName), objPtr, llvm.toString());
    }

}