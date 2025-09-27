package low.variables;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.variables.BinaryOpNode;


public class BinaryOpEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;

    public BinaryOpEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public String emit(BinaryOpNode node) {
        // 1. Avalia os operandos
        String leftLLVM = node.left.accept(visitor);
        String rightLLVM = node.right.accept(visitor);

        // 2. Extrai temp e tipo AST
        String leftTemp = extractTemp(leftLLVM);
        String rightTemp = extractTemp(rightLLVM);

        String leftTypeAST = extractType(leftLLVM);
        String rightTypeAST = extractType(rightLLVM);

        // 3. Converte tipos AST para LLVM
        String leftType = toLLVMType(leftTypeAST);
        String rightType = toLLVMType(rightTypeAST);

        // 4. Debug completo
        System.out.println("=== DEBUG BinaryOpEmitter ===");
        System.out.println("Operator: " + node.operator);
        System.out.println("LeftTemp: " + leftTemp + ", LeftType(AST): " + leftTypeAST + ", LeftType(LLVM): " + leftType);
        System.out.println("RightTemp: " + rightTemp + ", RightType(AST): " + rightTypeAST + ", RightType(LLVM): " + rightType);

        String resultTemp = temps.newTemp();
        StringBuilder llvm = new StringBuilder();

        llvm.append(leftLLVM).append("\n").append(rightLLVM).append("\n");

        // 5. INT
        if (leftType.equals("i32") && rightType.equals("i32")) {
            String op = switch (node.operator) {
                case "+" -> "add";
                case "-" -> "sub";
                case "*" -> "mul";
                case "/" -> "sdiv";
                case ">" -> "icmp sgt";
                case "<" -> "icmp slt";
                case ">=" -> "icmp sge";
                case "<=" -> "icmp sle";
                case "==" -> "icmp eq";
                case "!=" -> "icmp ne";
                default -> throw new RuntimeException("Operador inválido para int: " + node.operator);
            };
            llvm.append("  ").append(resultTemp).append(" = ").append(op)
                    .append(" i32 ").append(leftTemp).append(", ").append(rightTemp).append("\n")
                    .append(";;VAL:").append(resultTemp).append(";;TYPE:").append(op.startsWith("icmp") ? "i1" : "i32").append("\n");
        }
        // 6. DOUBLE
        else if (leftType.equals("double") || rightType.equals("double")) {
            if (leftType.equals("i32")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp).append(" = sitofp i32 ").append(leftTemp).append(" to double\n");
                leftTemp = tmp;
                leftType = "double";
            }
            if (rightType.equals("i32")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp).append(" = sitofp i32 ").append(rightTemp).append(" to double\n");
                rightTemp = tmp;
                rightType = "double";
            }

            String op = switch (node.operator) {
                case "+" -> "fadd";
                case "-" -> "fsub";
                case "*" -> "fmul";
                case "/" -> "fdiv";
                case ">" -> "fcmp ogt";
                case "<" -> "fcmp olt";
                case ">=" -> "fcmp oge";
                case "<=" -> "fcmp ole";
                case "==" -> "fcmp oeq";
                case "!=" -> "fcmp one";
                default -> throw new RuntimeException("Operador inválido para double: " + node.operator);
            };

            llvm.append("  ").append(resultTemp).append(" = ").append(op)
                    .append(" double ").append(leftTemp).append(", ").append(rightTemp).append("\n")
                    .append(";;VAL:").append(resultTemp).append(";;TYPE:").append(op.startsWith("fcmp") ? "i1" : "double").append("\n");
        }
        // 7. STRING
        else if (leftType.equals("i8*") && rightType.equals("i8*")) {
            if (node.operator.equals("+")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp).append(" = call i8* @concat_strings(i8* ")
                        .append(leftTemp).append(", i8* ").append(rightTemp).append(")\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:i8*\n");
            } else if (node.operator.equals("==") || node.operator.equals("!=")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp).append(" = call i1 @strcmp_eq(i8* ")
                        .append(leftTemp).append(", i8* ").append(rightTemp).append(")\n");
                if (node.operator.equals("!=")) {
                    String tmpNot = temps.newTemp();
                    llvm.append("  ").append(tmpNot).append(" = xor i1 ").append(tmp).append(", true\n");
                    resultTemp = tmpNot;
                } else {
                    resultTemp = tmp;
                }
                llvm.append(";;VAL:").append(resultTemp).append(";;TYPE:i1\n");
            } else {
                throw new RuntimeException("Operador inválido para string: " + node.operator);
            }
        } else {
            throw new RuntimeException("Tipos incompatíveis para operação: " + leftType + " " + node.operator + " " + rightType);
        }

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int typeIdx = code.lastIndexOf(";;TYPE:");
        return code.substring(typeIdx + 7).trim();
    }

    private String toLLVMType(String type) {
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string", "list" -> "i8*";
            default -> type; // já pode estar em LLVM
        };
    }
}
