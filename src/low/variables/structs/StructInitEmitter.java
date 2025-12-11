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

        String srcType  = info.getSourceType();
        String llvmType = info.getLLVMType();
        String varPtr   = getVarPtr(node.getName());

        // extrai T de Struct<Set<T>> ou Struct<Algo<T>>
        String inner = srcType.substring("Struct<".length(), srcType.length() - 1).trim();
        String elemType = null;

        int genericIdx = inner.indexOf('<');
        if (genericIdx != -1) {
            int close = inner.lastIndexOf('>');
            elemType = inner.substring(genericIdx + 1, close).trim();
        }

        String structLLVM = llvmType.substring(0, llvmType.length() - 1); // ex: %Set_Item

        StringBuilder sb = new StringBuilder();

        boolean escapes = visitor.escapesVar(node.getName());

        String tmpObj;
        String rawPtr = null;
        String sizeTmp = null;

        if (escapes) {
            sizeTmp = temps.newTemp();
            rawPtr  = temps.newTemp();
            tmpObj  = temps.newTemp();

            sb.append("  ").append(sizeTmp)
                    .append(" = getelementptr ").append(structLLVM)
                    .append(", ").append(structLLVM).append("* null, i32 1\n");

            sb.append("  ").append(sizeTmp)
                    .append(" = ptrtoint ").append(structLLVM)
                    .append("* ").append(sizeTmp).append(" to i64\n");

            sb.append("  ").append(rawPtr)
                    .append(" = call i8* @malloc(i64 ").append(sizeTmp).append(")\n");

            sb.append("  ").append(tmpObj)
                    .append(" = bitcast i8* ").append(rawPtr)
                    .append(" to ").append(structLLVM).append("*\n");

        } else {
            tmpObj = temps.newTemp();
            sb.append("  ").append(tmpObj)
                    .append(" = alloca ").append(structLLVM).append("\n");
        }

        // marca o valor para o seu sistema ;;VAL/;;TYPE
        sb.append(";;VAL:").append(tmpObj)
                .append(";;TYPE:").append(structLLVM).append("*\n");

        String tmpList  = temps.newTemp();
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

        // cria a lista
        sb.append("  ").append(tmpList)
                .append(" = call ").append(createListCall).append("\n");
        sb.append(";;VAL:").append(tmpList)
                .append(";;TYPE:").append(listPtrType).append("\n");

        // pega ponteiro para o campo 0 (que você assumiu ser a List<T>)
        sb.append("  ").append(fieldPtr)
                .append(" = getelementptr inbounds ")
                .append(structLLVM).append(", ").append(structLLVM)
                .append("* ").append(tmpObj).append(", i32 0, i32 0\n");

        // grava a lista no campo
        sb.append("  store ").append(listPtrType).append(" ").append(tmpList)
                .append(", ").append(listPtrType).append("* ").append(fieldPtr).append("\n");

        // grava o ponteiro do struct na variável
        sb.append("  store ").append(structLLVM).append("* ").append(tmpObj)
                .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

        return sb.toString();
    }

}