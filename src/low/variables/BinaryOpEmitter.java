package low.variables;
import ast.ASTNode;
import ast.variables.VariableNode;
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
        StringBuilder llvm = new StringBuilder();

        // Avalia left e right
        String leftLLVM = evaluateNode(node.left);
        String rightLLVM = evaluateNode(node.right);

        llvm.append(leftLLVM).append("\n").append(rightLLVM).append("\n");

        String leftTemp = extractTemp(leftLLVM);
        String rightTemp = extractTemp(rightLLVM);

        String leftTypeAST = extractType(leftLLVM);
        String rightTypeAST = extractType(rightLLVM);

        String leftType = toLLVMType(leftTypeAST);
        String rightType = toLLVMType(rightTypeAST);

        String resultTemp = temps.newTemp();

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
        else if (leftType.equals("double") || rightType.equals("double")) {
            // Converte int para double se necessário
            if (leftType.equals("i32")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp).append(" = sitofp i32 ").append(leftTemp).append(" to double\n;;VAL:").append(tmp).append(";;TYPE:double\n");
                leftTemp = tmp;
                leftType = "double";
            }
            if (rightType.equals("i32")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp).append(" = sitofp i32 ").append(rightTemp).append(" to double\n;;VAL:").append(tmp).append(";;TYPE:double\n");
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
        else if ((leftType.equals("%String*") || leftType.equals("i8*")) &&
                (rightType.equals("%String*") || rightType.equals("i8*"))) {

            // Converte i8* para %String* se necessário
            if (leftType.equals("i8*")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = call %String* @createString(i8* ").append(leftTemp).append(")\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:%String*\n");
                leftTemp = tmp;
                leftType = "%String*";
            }
            if (rightType.equals("i8*")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = call %String* @createString(i8* ").append(rightTemp).append(")\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:%String*\n");
                rightTemp = tmp;
                rightType = "%String*";
            }

            // Agora ambos são %String* e podemos aplicar o operador
            if (node.operator.equals("==")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = call i1 @strcmp_eq(%String* ").append(leftTemp)
                        .append(", %String* ").append(rightTemp).append(")\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:i1\n");
                resultTemp = tmp;
            } else if (node.operator.equals("!=")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = call i1 @strcmp_neq(%String* ").append(leftTemp)
                        .append(", %String* ").append(rightTemp).append(")\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:i1\n");
                resultTemp = tmp;
            } else if (node.operator.equals("+")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = call %String* @concatStrings(%String* ").append(leftTemp)
                        .append(", %String* ").append(rightTemp).append(")\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:%String*\n");
                resultTemp = tmp;
            } else {
                throw new RuntimeException("Operador inválido para %String*: " + node.operator);
            }
        }
        else {
            throw new RuntimeException("Tipos incompatíveis para operação: " + leftType + " " + node.operator + " " + rightType);
        }

        return llvm.toString();
    }

    private String evaluateNode(ASTNode node) {
        if (node instanceof VariableNode varNode) {
            return visitor.getVariableEmitter().emitLoad(varNode.getName());
        }
        return node.accept(visitor);
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int typeIdx = code.lastIndexOf(";;TYPE:");
        if (typeIdx == -1) throw new RuntimeException("Não encontrou ;;TYPE: em: " + code);
        int newlineIdx = code.indexOf("\n", typeIdx);
        return code.substring(typeIdx + 7, newlineIdx == -1 ? code.length() : newlineIdx).trim();
    }

    private String toLLVMType(String type) {
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string" -> "%String*";
            case  "List" -> "i8*";
            default -> type;
        };
    }
}
