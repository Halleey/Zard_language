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

    public String emit(VariableDeclarationNode node, TypeInfos info) {
        StringBuilder sb = new StringBuilder();

        Type type = info.getType(); // agora Type
        if (!(type instanceof StructType structType)) {
            throw new RuntimeException("Esperado StructType, encontrado: " + type);
        }

        String llvmType = info.getLLVMType(); // %Row*
        String varPtr   = getVarPtr(node.getName());

        StructNode structDef = visitor.getStructNode(structType.name());
        if (structDef == null) {
            throw new RuntimeException("Struct não encontrada: " + structType.name());
        }

        // LLVM pointers
        String structLLVMPtr = llvmType;               // %Row*
        String structLLVM = structLLVMPtr.substring(0, structLLVMPtr.length() - 1); // %Row

        String gepTmp  = temps.newTemp();
        String sizeTmp = temps.newTemp();
        String rawPtr  = temps.newTemp();
        String objPtr  = temps.newTemp();

        sb.append("  ").append(gepTmp)
                .append(" = getelementptr ").append(structLLVM)
                .append(", ").append(structLLVM).append("* null, i32 1\n");

        sb.append("  ").append(sizeTmp)
                .append(" = ptrtoint ").append(structLLVM)
                .append("* ").append(gepTmp).append(" to i64\n");

        sb.append("  ").append(rawPtr)
                .append(" = call i8* @malloc(i64 ").append(sizeTmp).append(")\n");

        sb.append("  ").append(objPtr)
                .append(" = bitcast i8* ").append(rawPtr)
                .append(" to ").append(structLLVM).append("*\n");

        sb.append(";;VAL:").append(objPtr)
                .append(";;TYPE:").append(structLLVM).append("*\n");

        // ====== Iterar campos da struct ======
        var fields = structDef.getFields();
        for (int i = 0; i < fields.size(); i++) {
            VariableDeclarationNode field = fields.get(i);
            Type fieldType = field.getType();

            if (!(fieldType instanceof ListType listType)) continue;

            // registrar tipo do elemento da lista
            visitor.registerListElementType(node.getName(), listType.elementType());

            // determinar LLVM type da lista
            String listLLVMType;
            String listCreateFn;

            Type elemType = listType.elementType();
            if (elemType.equals(PrimitiveTypes.INT)) {
                listLLVMType = "%struct.ArrayListInt*";
                listCreateFn = "@arraylist_create_int";
            } else if (elemType.equals(PrimitiveTypes.DOUBLE)) {
                listLLVMType = "%struct.ArrayListDouble*";
                listCreateFn = "@arraylist_create_double";
            } else if (elemType.equals(PrimitiveTypes.BOOL)) {
                listLLVMType = "%struct.ArrayListBool*";
                listCreateFn = "@arraylist_create_bool";
            } else {
                listLLVMType = "%ArrayList*";
                listCreateFn = "@arraylist_create";
            }

            String listTmp  = temps.newTemp();
            String fieldPtr = temps.newTemp();

            // criar lista
            sb.append("  ").append(listTmp)
                    .append(" = call ").append(listLLVMType)
                    .append(" ").append(listCreateFn)
                    .append("(i64 10)\n");

            sb.append(";;VAL:").append(listTmp)
                    .append(";;TYPE:").append(listLLVMType).append("\n");

            // ponteiro para campo
            sb.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVM).append(", ").append(structLLVM)
                    .append("* ").append(objPtr)
                    .append(", i32 0, i32 ").append(i).append("\n");

            sb.append("  store ").append(listLLVMType).append(" ").append(listTmp)
                    .append(", ").append(listLLVMType).append("* ").append(fieldPtr).append("\n");
        }

        // armazenar struct final na variável
        sb.append("  store ").append(structLLVM).append("* ").append(objPtr)
                .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

        return sb.toString();
    }
}