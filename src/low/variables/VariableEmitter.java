package low.variables;

import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;

import java.util.Map;

public class VariableEmitter {
    private final Map<String, String> varTypes;
    private final TempManager temps;
    private final GlobalStringManager globalStrings;
    private final LLVisitorMain visitor;

    public VariableEmitter(Map<String, String> varTypes, TempManager temps, GlobalStringManager globalStrings, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }

    public String emitAlloca(VariableDeclarationNode node) {
        String llvmType = switch (node.getType()) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string" -> "i8*";
            default -> throw new RuntimeException("Tipo desconhecido: " + node.getType());
        };
        varTypes.put(node.getName(), llvmType);
        return "  %" + node.getName() + " = alloca " + llvmType + "\n";
    }

    public String emitInit(VariableDeclarationNode node) {
        if (node.initializer == null) return "";
        String llvmType = varTypes.get(node.getName());

        if (node.initializer instanceof LiteralNode lit) {
            Object val = lit.value.getValue();
            if (llvmType.equals("double") && val instanceof Integer) val = ((Integer) val).doubleValue();

            StringBuilder llvm = new StringBuilder();
            switch (llvmType) {
                case "i1" -> llvm.append("  store i1 ").append((Boolean) val ? "1" : "0")
                        .append(", i1* %").append(node.getName()).append("\n");
                case "i32" -> llvm.append("  store i32 ").append(val)
                        .append(", i32* %").append(node.getName()).append("\n");
                case "double" -> llvm.append("  store double ").append(val)
                        .append(", double* %").append(node.getName()).append("\n");
                case "i8*" -> {
                    String strName = globalStrings.getOrCreateString((String) val);
                    int len = ((String) val).length() + 2;
                    llvm.append("  store i8* getelementptr ([")
                            .append(len).append(" x i8], [").append(len).append(" x i8]* ")
                            .append(strName).append(", i32 0, i32 0), i8** %")
                            .append(node.getName()).append("\n");
                }
            }
            return llvm.toString();
        }

        // Expressões complexas → gerar LLVM e fazer store no resultado
        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractTemp(exprLLVM);
        return exprLLVM + "\n  store " + llvmType + " " + temp + ", " + llvmType + "* %" + node.getName() + "\n";
    }

    public String emitLoad(String name) {
        String type = varTypes.getOrDefault(name, "i32");
        String tmp = temps.newTemp();
        return "  " + tmp + " = load " + type + ", " + type + "* %" + name + "\n;;VAL:" + tmp + ";;TYPE:" + type + "\n";
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}