package low.variables.exps;


import ast.variables.VariableDeclarationNode;

import low.TempManager;
import low.functions.TypeMapper;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.variables.VariableEmitter;

import java.util.Map;


public class AllocaEmitter {

    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final VariableEmitter varEmitter; // precisa para pegar o scope ID

    public AllocaEmitter(Map<String, TypeInfos> varTypes,
                         TempManager temps,
                         LLVisitorMain visitor,
                         VariableEmitter varEmitter) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.visitor = visitor;
        this.varEmitter = varEmitter;
    }

    private String mapLLVMType(String sourceType) {
        return new TypeMapper().toLLVM(sourceType);
    }

    private String newPtr(String varName) {
        // Se estamos no escopo global (escopo raiz = 0), não adiciona sufixo
        int currentScopeId = varEmitter.getScopeId();
        String ptr;
        if (currentScopeId == 0) {
            ptr = "%" + varName; // nome “limpo” para global
        } else {
            ptr = "%" + varName + "_" + currentScopeId; // nome único para escopos internos
        }

        varEmitter.registerVarPtr(varName, ptr);
        return ptr;
    }


    public String emit(VariableDeclarationNode node) {
        String srcType = node.getType();
        String varName = node.getName();
        String ptr = newPtr(varName);

        String llvmType;
        String elemType = null;

        switch (srcType) {
            case "string" -> {
                llvmType = "%String*";
                varTypes.put(varName, new TypeInfos(srcType, llvmType, null));
                return "  " + ptr + " = alloca %String*\n"
                        + ";;VAL:" + ptr + ";;TYPE:%String*\n";
            }
            case "char" -> {
                llvmType = "i8";
                varTypes.put(varName, new TypeInfos(srcType, llvmType, null));
                return "  " + ptr + " = alloca i8\n"
                        + ";;VAL:" + ptr + ";;TYPE:i8\n";
            }
            case "List<int>" -> {
                llvmType = "%struct.ArrayListInt*";
                elemType = "int";
                varTypes.put(varName, new TypeInfos(srcType, llvmType, elemType));
                return "  " + ptr + " = alloca %struct.ArrayListInt*\n"
                        + ";;VAL:" + ptr + ";;TYPE:%struct.ArrayListInt*\n";
            }
            case "List<double>" -> {
                llvmType = "%struct.ArrayListDouble*";
                elemType = "double";
                varTypes.put(varName, new TypeInfos(srcType, llvmType, elemType));
                return "  " + ptr + " = alloca %struct.ArrayListDouble*\n"
                        + ";;VAL:" + ptr + ";;TYPE:%struct.ArrayListDouble*\n";
            }
            case "List<boolean>" -> {
                llvmType = "%struct.ArrayListBool*";
                elemType = "boolean";
                varTypes.put(varName, new TypeInfos(srcType, llvmType, elemType));
                return "  " + ptr + " = alloca %struct.ArrayListBool*\n"
                        + ";;VAL:" + ptr + ";;TYPE:%struct.ArrayListBool*\n";
            }
            default -> {
                if (srcType.startsWith("List<")) {
                    String inner = srcType.substring(5, srcType.length() - 1).trim();
                    elemType = inner;
                    llvmType = "%ArrayList*";
                    varTypes.put(varName, new TypeInfos(srcType, llvmType, elemType));
                    return "  " + ptr + " = alloca %ArrayList*\n"
                            + ";;VAL:" + ptr + ";;TYPE:%ArrayList*\n";
                }
                llvmType = mapLLVMType(srcType);
                varTypes.put(varName, new TypeInfos(srcType, llvmType, null));
                return "  " + ptr + " = alloca " + llvmType + "\n"
                        + ";;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
            }
        }
    }
}
