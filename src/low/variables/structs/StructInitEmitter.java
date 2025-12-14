package low.variables.structs;


import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.Map;


public class StructInitEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final Map<String, String> localVars;

    public StructInitEmitter(
            TempManager temps,
            LLVisitorMain visitor,
            Map<String, String> localVars
    ) {
        this.temps = temps;
        this.visitor = visitor;
        this.localVars = localVars;
    }

    private String getVarPtr(String name) {
        String ptr = localVars.get(name);
        if (ptr == null) {
            throw new RuntimeException("Ptr não encontrado para variável: " + name);
        }
        return ptr;
    }

    public String emit(VariableDeclarationNode node, TypeInfos info) {

        StringBuilder sb = new StringBuilder();

        String srcType  = info.getSourceType();   // Struct<Row>
        String llvmType = info.getLLVMType();     // %Row*
        String varPtr   = getVarPtr(node.getName());

        // ==== resolver nome da struct ====
        String structName = srcType.substring("Struct<".length(), srcType.length() - 1).trim();
        StructNode structDef = visitor.getStructNode(structName);

        if (structDef == null) {
            throw new RuntimeException("Struct não encontrada: " + structName);
        }

        // %Row*
        String structLLVMPtr = llvmType;
        // %Row
        String structLLVM = structLLVMPtr.substring(0, structLLVMPtr.length() - 1);

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

        var fields = structDef.getFields();

        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            String fieldType = field.getType();

            if (!fieldType.startsWith("List<")) continue;

            String elemType = fieldType.substring(5, fieldType.length() - 1).trim();
            visitor.registerListElementType(node.getName(), elemType);

            String listLLVMType;
            String listCreateFn;

            switch (elemType) {
                case "int" -> {
                    listLLVMType = "%struct.ArrayListInt*";
                    listCreateFn = "@arraylist_create_int";
                }
                case "double" -> {
                    listLLVMType = "%struct.ArrayListDouble*";
                    listCreateFn = "@arraylist_create_double";
                }
                case "boolean" -> {
                    listLLVMType = "%struct.ArrayListBool*";
                    listCreateFn = "@arraylist_create_bool";
                }
                default -> {
                    listLLVMType = "%ArrayList*";
                    listCreateFn = "@arraylist_create";
                }
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

        sb.append("  store ").append(structLLVM).append("* ").append(objPtr)
                .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

        return sb.toString();
    }
}
