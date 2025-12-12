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

        String srcType  = info.getSourceType(); // ex: Struct<Set<int>> ou Struct<Set>
        String llvmType = info.getLLVMType();   // ex: %Set_int* (ponteiro)
        String varPtr   = getVarPtr(node.getName()); // ex: %s (alloca %Set_int**)

        // structLLVM sem '*'
        String structLLVM = llvmType.endsWith("*")
                ? llvmType.substring(0, llvmType.length() - 1)
                : llvmType;

        // extrai inner: Set<int> de Struct<Set<int>>
        String inner = extractInnerStructName(srcType); // "Set<int>" ou "Set"
        String elemType = extractGenericArg(inner);     // "int" / "string" / null

        if (elemType == null) elemType = "?"; // IMPORTANTÍSSIMO para Set s; antes de especializar

        StringBuilder sb = new StringBuilder();

        boolean escapes = visitor.escapesVar(node.getName());

        String tmpObj;

        if (escapes) {
            // ✅ SSA correto: 2 temps diferentes
            String gepTmp   = temps.newTemp();
            String sizeTmp  = temps.newTemp();
            String rawPtr   = temps.newTemp();
            tmpObj          = temps.newTemp();

            sb.append("  ").append(gepTmp)
                    .append(" = getelementptr ").append(structLLVM)
                    .append(", ").append(structLLVM).append("* null, i32 1\n");

            sb.append("  ").append(sizeTmp)
                    .append(" = ptrtoint ").append(structLLVM)
                    .append("* ").append(gepTmp).append(" to i64\n");

            sb.append("  ").append(rawPtr)
                    .append(" = call i8* @malloc(i64 ").append(sizeTmp).append(")\n");

            sb.append("  ").append(tmpObj)
                    .append(" = bitcast i8* ").append(rawPtr)
                    .append(" to ").append(structLLVM).append("*\n");

        } else {
            // stack allocate
            tmpObj = temps.newTemp();
            sb.append("  ").append(tmpObj)
                    .append(" = alloca ").append(structLLVM).append("\n");
        }

        // marca ;;VAL/;;TYPE
        sb.append(";;VAL:").append(tmpObj)
                .append(";;TYPE:").append(structLLVM).append("*\n");

        // registra tipo do elemento (seu pipeline usa isso em ListAddEmitter etc)
        visitor.registerListElementType(node.getName(), elemType);

        // cria lista + armazena no campo 0
        emitDefaultListInit(sb, tmpObj, structLLVM, elemType);

        // grava ponteiro do struct na variável
        sb.append("  store ").append(structLLVM).append("* ").append(tmpObj)
                .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

        return sb.toString();
    }

    private void emitDefaultListInit(StringBuilder sb,
                                     String structPtrTmp,
                                     String structLLVM,
                                     String elemType) {

        String tmpList  = temps.newTemp();
        String fieldPtr = temps.newTemp();

        // escolha do runtime create + tipo do ponteiro de lista
        String createCall;
        String listPtrType;

        switch (elemType) {
            case "int" -> {
                createCall = "%struct.ArrayListInt* @arraylist_create_int(i64 10)";
                listPtrType = "%struct.ArrayListInt*";
            }
            case "double" -> {
                createCall = "%struct.ArrayListDouble* @arraylist_create_double(i64 10)";
                listPtrType = "%struct.ArrayListDouble*";
            }
            case "boolean" -> {
                createCall = "%struct.ArrayListBool* @arraylist_create_bool(i64 10)";
                listPtrType = "%struct.ArrayListBool*";
            }
            case "string" -> {
                createCall = "%ArrayList* @arraylist_create(i64 10)";
                listPtrType = "%ArrayList*";
            }
            default -> {
                // "?" ou tipos ainda não resolvidos → lista genérica ptr
                createCall = "%ArrayList* @arraylist_create(i64 10)";
                listPtrType = "%ArrayList*";
            }
        }

        // call create
        sb.append("  ").append(tmpList)
                .append(" = call ").append(createCall).append("\n");
        sb.append(";;VAL:").append(tmpList)
                .append(";;TYPE:").append(listPtrType).append("\n");

        // gep field 0
        sb.append("  ").append(fieldPtr)
                .append(" = getelementptr inbounds ")
                .append(structLLVM).append(", ").append(structLLVM)
                .append("* ").append(structPtrTmp).append(", i32 0, i32 0\n");

        // store list into field 0
        sb.append("  store ").append(listPtrType).append(" ").append(tmpList)
                .append(", ").append(listPtrType).append("* ").append(fieldPtr).append("\n");
    }

    private String extractInnerStructName(String srcType) {
        // srcType: "Struct<Set<int>>" ou "Struct<Set>"
        if (srcType == null) return "";
        if (!srcType.startsWith("Struct<") || !srcType.endsWith(">")) return srcType;

        String inner = srcType.substring("Struct<".length(), srcType.length() - 1).trim();
        return inner;
    }

    private String extractGenericArg(String inner) {
        // inner: "Set<int>" -> "int"
        // inner: "Set" -> null
        if (inner == null) return null;

        int genericIdx = inner.indexOf('<');
        if (genericIdx == -1) return null;

        int close = inner.lastIndexOf('>');
        if (close == -1 || close <= genericIdx) return null;

        return inner.substring(genericIdx + 1, close).trim();
    }
}
