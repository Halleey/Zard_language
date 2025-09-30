package low.variables;
import ast.inputs.InputNode;
import ast.lists.ListNode;
import low.inputs.InputEmitter;
import low.lists.ListEmitter;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.variables.VariableDeclarationNode;

import java.util.HashMap;
import java.util.Map;
public class VariableEmitter {
    private final Map<String, String> varTypes; // nome -> tipo LLVM
    private final TempManager temps;
    private final GlobalStringManager globalStrings;
    private final LLVisitorMain visitor;
    private final Map<String, String> localVars = new HashMap<>(); // nome -> ponteiro (%var)

    public VariableEmitter(Map<String, String> varTypes, TempManager temps,
                           GlobalStringManager globalStrings, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }

    public String mapLLVMType(String type) {
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string", "List" -> "i8*";
            default -> type; // já é LLVM ou void
        };
    }

    public void registerVarPtr(String name, String ptr) {
        localVars.put(name, ptr);
    }

    public String emitAlloca(VariableDeclarationNode node) {
        String llvmType = mapLLVMType(node.getType());
        varTypes.put(node.getName(), llvmType);

        if ("List".equals(node.getType())) visitor.registerListVar(node.getName());

        String cleanName = node.getName().startsWith("%") ? node.getName().substring(1) : node.getName();
        String ptrName = "%" + cleanName;
        localVars.put(node.getName(), ptrName);

        return "  " + ptrName + " = alloca " + llvmType + "\n;;VAL:" + ptrName + ";;TYPE:" + llvmType + "\n";
    }

    public String emitInit(VariableDeclarationNode node) {
        String llvmType = varTypes.get(node.getName());
        if (node.initializer == null) {
            return "List".equals(node.getType())
                    ? emitStore(node.getName(), "i8*", callArrayListCreate())
                    : "";
        }

        if (node.initializer instanceof ast.variables.LiteralNode lit) {
            return emitLiteralInit(node.getName(), llvmType, lit.value.getValue());
        }

        String exprLLVM;
        if (node.initializer instanceof ListNode listNode) {
            exprLLVM = new ListEmitter(temps, globalStrings).emit(listNode, visitor);
        } else if (node.initializer instanceof InputNode inputNode) {
            exprLLVM = new InputEmitter(temps, globalStrings).emit(inputNode, llvmType);
        } else {
            exprLLVM = node.initializer.accept(visitor);
        }


        String temp = extractTemp(exprLLVM);
        return exprLLVM + emitStore(node.getName(), llvmType, temp);
    }

    private String emitLiteralInit(String name, String llvmType, Object val) {
        if ("double".equals(llvmType) && val instanceof Integer i) val = i.doubleValue();
        return switch (llvmType) {
            case "i1" -> emitStore(name, llvmType, (Boolean) val ? "1" : "0");
            case "i32", "double" -> emitStore(name, llvmType, val.toString());
            case "i8*" -> {
                String strName = globalStrings.getOrCreateString((String) val);
                int len = ((String) val).length() + 1;
                yield emitStore(name, llvmType,
                        "getelementptr ([" + len + " x i8], [" + len + " x i8]* "
                                + strName + ", i32 0, i32 0)");
            }
            default -> "";
        };
    }

    private String emitStore(String varName, String type, String value) {
        return "  store " + type + " " + value + ", " + type + "* %" + varName + "\n";
    }

    private String callArrayListCreate() {
        String tmp = temps.newTemp();
        return tmp + " = call i8* @arraylist_create(i64 4)\n;;VAL:" + tmp + ";;TYPE:i8*\n";
    }

    public String emitLoad(String name) {
        String type = varTypes.get(name);
        if (type == null) throw new RuntimeException("Variável não alocada: " + name);

        type = mapLLVMType(type);
        String tmp = temps.newTemp();
        String ptr = localVars.get(name);
        if (ptr == null) throw new RuntimeException("Variável não alocada: " + name);

        return "  " + tmp + " = load " + type + ", " + type + "* " + ptr +
                "\n;;VAL:" + tmp + ";;TYPE:" + type + "\n";
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    public String getVarPtr(String name) {
        return localVars.get(name);
    }
}
