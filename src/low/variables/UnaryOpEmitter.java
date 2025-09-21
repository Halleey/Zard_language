package low.variables;

import ast.ASTNode;
import low.TempManager;
import ast.variables.VariableNode;

import java.util.Map;

public class UnaryOpEmitter {
    private final Map<String, String> varTypes;
    private final TempManager temps;

    public UnaryOpEmitter(Map<String, String> varTypes, TempManager temps) {
        this.varTypes = varTypes;
        this.temps = temps;
    }

    public String emit(String operator, ASTNode expr) {
        StringBuilder llvm = new StringBuilder();

        // Se for variável
        if (expr instanceof VariableNode varNode) {
            String varName = varNode.getName();
            String llvmType = varTypes.get(varName);

            if (llvmType == null) {
                throw new RuntimeException("Variável não declarada: " + varName);
            }

            // Load do valor
            String temp = temps.newTemp();
            llvm.append("  ").append(temp)
                    .append(" = load ").append(llvmType).append(", ")
                    .append(llvmType).append("* %").append(varName).append("\n");

            switch (operator) {
                case "++" -> {
                    String inc = temps.newTemp();
                    if (llvmType.equals("i32")) {
                        llvm.append("  ").append(inc).append(" = add i32 ").append(temp).append(", 1\n");
                    } else if (llvmType.equals("double")) {
                        llvm.append("  ").append(inc).append(" = fadd double ").append(temp).append(", 1.0\n");
                    } else {
                        throw new RuntimeException("++ não suportado para " + llvmType);
                    }
                    // Store de volta
                    llvm.append("  store ").append(llvmType).append(" ").append(inc)
                            .append(", ").append(llvmType).append("* %").append(varName).append("\n");
                    return llvm.toString();
                }
                case "--" -> {
                    String dec = temps.newTemp();
                    if (llvmType.equals("i32")) {
                        llvm.append("  ").append(dec).append(" = sub i32 ").append(temp).append(", 1\n");
                    } else if (llvmType.equals("double")) {
                        llvm.append("  ").append(dec).append(" = fsub double ").append(temp).append(", 1.0\n");
                    } else {
                        throw new RuntimeException("-- não suportado para " + llvmType);
                    }
                    llvm.append("  store ").append(llvmType).append(" ").append(dec)
                            .append(", ").append(llvmType).append("* %").append(varName).append("\n");
                    return llvm.toString();
                }
                case "+" -> {
                    // operador unário + (basicamente não faz nada)
                    return llvm.toString();
                }
                case "-" -> {
                    String neg = temps.newTemp();
                    if (llvmType.equals("i32")) {
                        llvm.append("  ").append(neg).append(" = sub i32 0, ").append(temp).append("\n");
                    } else if (llvmType.equals("double")) {
                        llvm.append("  ").append(neg).append(" = fsub double 0.0, ").append(temp).append("\n");
                    } else {
                        throw new RuntimeException("Unário - não suportado para " + llvmType);
                    }
                    return llvm.toString();
                }
            }
        }

        throw new RuntimeException("Operador unário " + operator + " não suportado ainda em expressões complexas.");
    }
}
