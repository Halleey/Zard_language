package low.variables;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.variables.LiteralNode;
import low.TempManager;
import ast.variables.VariableNode;

import java.util.Map;
public class UnaryOpEmitter {
    private final Map<String, String> varTypes;
    private final TempManager temps;
    private final VariableEmitter varEmitter; // precisa do emissor de variáveis

    public UnaryOpEmitter(Map<String, String> varTypes, TempManager temps, VariableEmitter varEmitter) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.varEmitter = varEmitter;
    }

    private String normalizeType(String type) {
        return switch (type) {
            case "int", "i32" -> "i32";
            case "double" -> "double";
            case "string", "i8*", "ptr" -> "i8*";
            case "boolean", "bool", "i1" -> "i1";
            default -> type; // deixa passar tipos customizados, se houver
        };
    }

    public String emit(String operator, ASTNode expr) {
        StringBuilder llvm = new StringBuilder();


        if (expr instanceof LiteralNode literal) {
            TypedValue val = literal.getValue();
            String llvmType;
            String valueStr;

            switch (val.getType()) {
                case "int" -> {
                    llvmType = "i32";
                    valueStr = Integer.toString((Integer) val.getValue());
                }
                case "double" -> {
                    llvmType = "double";
                    valueStr = Double.toString((Double) val.getValue());
                }
                default -> throw new RuntimeException("Unário " + operator + " não suportado para " + val.getType());
            }

            String temp = temps.newTemp();

            switch (operator) {
                case "-" -> {
                    if (llvmType.equals("i32")) {
                        llvm.append("  ").append(temp).append(" = sub i32 0, ").append(valueStr).append("\n");
                    } else if (llvmType.equals("double")) {
                        llvm.append("  ").append(temp).append(" = fsub double 0.0, ").append(valueStr).append("\n");
                    }
                }
                case "+" -> {
                    // operador unário +: apenas retorna o valor, podemos criar um temp para uniformidade
                    if (llvmType.equals("i32")) {
                        llvm.append("  ").append(temp).append(" = add i32 0, ").append(valueStr).append("\n");
                    } else if (llvmType.equals("double")) {
                        llvm.append("  ").append(temp).append(" = fadd double 0.0, ").append(valueStr).append("\n");
                    }
                }
                default -> throw new RuntimeException("Operador unário " + operator + " não suportado para literals");
            }

            // Adiciona os marcadores obrigatórios
            llvm.append(";;VAL:").append(temp).append(";;TYPE:").append(llvmType).append("\n");

            return llvm.toString();
        }


        // Só tratamos variáveis por enquanto
        if (expr instanceof VariableNode varNode) {
            String varName = varNode.getName();
            String llvmType = varTypes.get(varName);

            if (llvmType == null) {
                throw new RuntimeException("Variável não declarada: " + varName);
            }

            // normaliza o tipo (int -> i32, boolean -> i1, etc)
            llvmType = normalizeType(llvmType);

            // pega o ponteiro correto (pode ser %a.addr dentro de função, ou global)
            String ptr = varEmitter.getVarPtr(varName);
            if (ptr == null) {
                throw new RuntimeException("Ponteiro não encontrado para variável: " + varName);
            }

            // Load do valor atual
            String temp = temps.newTemp();
            llvm.append("  ").append(temp)
                    .append(" = load ").append(llvmType).append(", ")
                    .append(llvmType).append("* ").append(ptr).append("\n");

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
                            .append(", ").append(llvmType).append("* ").append(ptr).append("\n");
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
                            .append(", ").append(llvmType).append("* ").append(ptr).append("\n");
                    return llvm.toString();
                }
                case "+" -> {
                    // operador unário + (basicamente não faz nada além do load)
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
