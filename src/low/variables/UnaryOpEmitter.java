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
    private final VariableEmitter varEmitter;

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
            default -> type;
        };
    }

    public String emit(String operator, ASTNode expr) {
        StringBuilder llvm = new StringBuilder();

        if (expr instanceof LiteralNode literal) {
            TypedValue val = literal.getValue();
            String llvmType;
            String valueStr;

            switch (val.getType()) {
                case "int" -> { llvmType = "i32"; valueStr = val.getValue().toString(); }
                case "double" -> { llvmType = "double"; valueStr = val.getValue().toString(); }
                default -> throw new RuntimeException("Unário " + operator + " não suportado para " + val.getType());
            }

            String temp = temps.newTemp();
            switch (operator) {
                case "-" -> llvm.append("  ").append(temp).append(" = ").append(llvmType.equals("i32") ? "sub i32 0, " : "fsub double 0.0, ").append(valueStr).append("\n");
                case "+" -> llvm.append("  ").append(temp).append(" = ").append(llvmType.equals("i32") ? "add i32 0, " : "fadd double 0.0, ").append(valueStr).append("\n");
                default -> throw new RuntimeException("Operador unário " + operator + " não suportado para literal");
            }
            llvm.append(";;VAL:").append(temp).append(";;TYPE:").append(llvmType).append("\n");
            return llvm.toString();
        }

        if (expr instanceof VariableNode varNode) {
            String varName = varNode.getName();
            String llvmType = normalizeType(varTypes.get(varName));
            String ptr = varEmitter.getVarPtr(varName);
            if (ptr == null) throw new RuntimeException("Ponteiro não encontrado para variável: " + varName);

            String temp = temps.newTemp();
            llvm.append("  ").append(temp).append(" = load ").append(llvmType).append(", ").append(llvmType).append("* ").append(ptr).append("\n");

            switch (operator) {
                case "++" -> {
                    String inc = temps.newTemp();
                    llvm.append("  ").append(inc).append(" = ").append(llvmType.equals("i32") ? "add i32 " : "fadd double ").append(temp).append(", ").append(llvmType.equals("i32") ? "1" : "1.0").append("\n");
                    llvm.append("  store ").append(llvmType).append(" ").append(inc).append(", ").append(llvmType).append("* ").append(ptr).append("\n");
                    llvm.append(";;VAL:").append(inc).append(";;TYPE:").append(llvmType).append("\n");
                    return llvm.toString();
                }
                case "--" -> {
                    String dec = temps.newTemp();
                    llvm.append("  ").append(dec).append(" = ").append(llvmType.equals("i32") ? "sub i32 " : "fsub double ").append(temp).append(", ").append(llvmType.equals("i32") ? "1" : "1.0").append("\n");
                    llvm.append("  store ").append(llvmType).append(" ").append(dec).append(", ").append(llvmType).append("* ").append(ptr).append("\n");
                    llvm.append(";;VAL:").append(dec).append(";;TYPE:").append(llvmType).append("\n");
                    return llvm.toString();
                }
                case "+" -> {
                    llvm.append(";;VAL:").append(temp).append(";;TYPE:").append(llvmType).append("\n");
                    return llvm.toString();
                }
                case "-" -> {
                    String neg = temps.newTemp();
                    llvm.append("  ").append(neg).append(" = ").append(llvmType.equals("i32") ? "sub i32 0, " : "fsub double 0.0, ").append(temp).append("\n");
                    llvm.append(";;VAL:").append(neg).append(";;TYPE:").append(llvmType).append("\n");
                    return llvm.toString();
                }
            }
        }

        throw new RuntimeException("Operador unário " + operator + " não suportado em expressões complexas.");
    }
}
