package low.variables;
import ast.lists.ListNode;
import low.lists.ListEmitter;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;

import java.util.HashMap;
import java.util.Map;

public class VariableEmitter {
    private final Map<String, String> varTypes;
    private final TempManager temps;
    private final GlobalStringManager globalStrings;
    private final LLVisitorMain visitor;

    private final Map<String, String> localVars = new HashMap<>();

    public VariableEmitter(Map<String, String> varTypes, TempManager temps, GlobalStringManager globalStrings, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }

    // Aloca a variável na stack e registra no mapa
    public String emitAlloca(VariableDeclarationNode node) {
        String llvmType = switch (node.getType()) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string", "list", "var" -> "i8*";
            default -> throw new RuntimeException("Tipo desconhecido: " + node.getType());
        };

        varTypes.put(node.getName(), llvmType);
        if (node.getType().equals("list")) visitor.registerListVar(node.getName());

        String ptrName = "%" + node.getName() + ".addr";
        localVars.put(node.getName(), ptrName);

        return "  " + ptrName + " = alloca " + llvmType + "\n";
    }

    public String getVarPtr(String name) {
        return localVars.get(name);
    }

    public String emitLoad(String name) {
        String llvmType = varTypes.getOrDefault(name, "i32");
        String ptr = getVarPtr(name);
        if (ptr == null) throw new RuntimeException("Variável não alocada: " + name);
        String tmp = temps.newTemp();
        return "  " + tmp + " = load " + llvmType + ", " + llvmType + "* " + ptr + "\n" +
                ";;VAL:" + tmp + ";;TYPE:" + llvmType + "\n";
    }

    public String emitInit(VariableDeclarationNode node) {
        String llvmType = varTypes.get(node.getName());
        String ptr = getVarPtr(node.getName());

        if (node.initializer instanceof LiteralNode lit) {
            Object val = lit.value.getValue();
            if (llvmType.equals("double") && val instanceof Integer) val = ((Integer) val).doubleValue();
            StringBuilder llvm = new StringBuilder();

            switch (llvmType) {
                case "i1" -> llvm.append("  store i1 ").append((Boolean) val ? 1 : 0).append(", i1* ").append(ptr).append("\n")
                        .append(";;VAL:").append(val).append(";;TYPE:i1\n");
                case "i32" -> llvm.append("  store i32 ").append(val).append(", i32* ").append(ptr).append("\n")
                        .append(";;VAL:").append(val).append(";;TYPE:i32\n");
                case "double" -> llvm.append("  store double ").append(val).append(", double* ").append(ptr).append("\n")
                        .append(";;VAL:").append(val).append(";;TYPE:double\n");
                case "i8*" -> {
                    String strName = globalStrings.getOrCreateString((String) val);
                    int len = ((String) val).length() + 1;
                    llvm.append("  store i8* getelementptr ([")
                            .append(len).append(" x i8], [").append(len).append(" x i8]* ")
                            .append(strName).append(", i32 0, i32 0), i8** ")
                            .append(ptr).append("\n")
                            .append(";;VAL:").append(strName).append(";;TYPE:i8*\n");
                }
            }
            return llvm.toString();
        }

        String exprLLVM = node.initializer.accept(visitor);
        String tmp = extractTemp(exprLLVM);
        return exprLLVM +
                "  store " + llvmType + " " + tmp + ", " + llvmType + "* " + ptr + "\n" +
                ";;VAL:" + tmp + ";;TYPE:" + llvmType + "\n";
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
