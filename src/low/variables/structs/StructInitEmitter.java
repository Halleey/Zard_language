package low.variables.structs;


import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.Map;

public class StructInitEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final Map<String, String> localVars;

    public StructInitEmitter(TempManager temps,
                             LLVisitorMain visitor,
                             Map<String, String> localVars) {
        this.temps = temps;
        this.visitor = visitor;
        this.localVars = localVars;
    }

    private String getVarPtr(String name) {
        String ptr = localVars.get(name);
        if (ptr == null) {
            throw new RuntimeException("Ptr not found for variable: " + name);
        }
        return ptr;
    }

    public String emit(VariableDeclarationNode node, TypeInfos info) {

        String srcType = info.getSourceType();
        String llvmType = info.getLLVMType();
        String varPtr = getVarPtr(node.getName());


        String inner = srcType.substring("Struct<".length(), srcType.length() - 1).trim();
        String elemType = null;

        int genericIdx = inner.indexOf('<');
        if (genericIdx != -1) {
            int close = inner.lastIndexOf('>');
            elemType = inner.substring(genericIdx + 1, close).trim();
        }

        String structLLVM = llvmType.substring(0, llvmType.length() - 1);
        String tmpObj = temps.newTemp();
        String tmpList = temps.newTemp();
        String fieldPtr = temps.newTemp();

        visitor.registerListElementType(node.getName(), elemType);

        String createListCall;
        String listPtrType;

        switch (elemType) {
            case "int" -> {
                createListCall = "%struct.ArrayListInt* @arraylist_create_int(i64 10)";
                listPtrType = "%struct.ArrayListInt*";
            }
            case "double" -> {
                createListCall = "%struct.ArrayListDouble* @arraylist_create_double(i64 10)";
                listPtrType = "%struct.ArrayListDouble*";
            }
            case "boolean" -> {
                createListCall = "%struct.ArrayListBool* @arraylist_create_bool(i64 10)";
                listPtrType = "%struct.ArrayListBool*";
            }
            case "string" -> {
                createListCall = "%ArrayList* @arraylist_create(i64 10)";
                listPtrType = "%ArrayList*";
            }
            default -> {
                createListCall = "%ArrayList* @arraylist_create(i64 10)";
                listPtrType = "%ArrayList*";
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("  ").append(tmpObj).append(" = alloca ").append(structLLVM).append("\n");
        sb.append(";;VAL:").append(tmpObj).append(";;TYPE:").append(structLLVM).append("*\n");

        sb.append("  ").append(tmpList).append(" = call ").append(createListCall).append("\n");
        sb.append(";;VAL:").append(tmpList).append(";;TYPE:").append(listPtrType).append("\n");

        sb.append("  ").append(fieldPtr)
                .append(" = getelementptr inbounds ")
                .append(structLLVM).append(", ").append(structLLVM)
                .append("* ").append(tmpObj).append(", i32 0, i32 0\n");

        sb.append("  store ").append(listPtrType).append(" ").append(tmpList)
                .append(", ").append(listPtrType).append("* ").append(fieldPtr).append("\n");

        sb.append("  store ").append(structLLVM).append("* ").append(tmpObj)
                .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

        return sb.toString();
    }
}