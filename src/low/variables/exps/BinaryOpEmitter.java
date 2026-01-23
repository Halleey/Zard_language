package low.variables.exps;
import ast.ASTNode;
import ast.variables.VariableNode;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.expressions.BinaryOpNode;
public class BinaryOpEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;

    public BinaryOpEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public String emit(BinaryOpNode node) {
        String op = node.operator;

        if ("&&".equals(op) || "||".equals(op)) {
            return emitLogicalShortCircuit(node, op);
        }

        StringBuilder llvm = new StringBuilder();

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

        if (leftType.equals("i1") && rightType.equals("i1")) {

            String bop = switch (op) {
                case "==" -> "icmp eq";
                case "!=" -> "icmp ne";
                default -> throw new RuntimeException("Operador inválido para boolean: " + op);
            };
            llvm.append("  ").append(resultTemp).append(" = ").append(bop)
                    .append(" i1 ").append(leftTemp).append(", ").append(rightTemp).append("\n")
                    .append(";;VAL:").append(resultTemp).append(";;TYPE:i1\n");
            return llvm.toString();
        }

        if (leftType.equals("i8") && rightType.equals("i8")) {
            String bop = switch (op) {
                case "==" -> "icmp eq";
                case "!=" -> "icmp ne";
                case ">" -> "icmp sgt";
                case "<" -> "icmp slt";
                case ">=" -> "icmp sge";
                case "<=" -> "icmp sle";
                default -> throw new RuntimeException("Operador inválido para char (i8): " + op);
            };
            llvm.append("  ").append(resultTemp).append(" = ").append(bop)
                    .append(" i8 ").append(leftTemp).append(", ").append(rightTemp).append("\n")
                    .append(";;VAL:").append(resultTemp).append(";;TYPE:i1\n");
            return llvm.toString();
        }
        if (leftType.equals("i32") && rightType.equals("i32")) {
            String bop = switch (op) {
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
                default -> throw new RuntimeException("Operador inválido para int: " + op);
            };
            llvm.append("  ").append(resultTemp).append(" = ").append(bop)
                    .append(" i32 ").append(leftTemp).append(", ").append(rightTemp).append("\n")
                    .append(";;VAL:").append(resultTemp).append(";;TYPE:")
                    .append(bop.startsWith("icmp") ? "i1" : "i32").append("\n");
            return llvm.toString();
        }

        if (leftType.equals("double") || rightType.equals("double")) {

            if (leftType.equals("i32")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp).append(" = sitofp i32 ").append(leftTemp).append(" to double\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:double\n");
                leftTemp = tmp;
            }
            if (rightType.equals("i32")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp).append(" = sitofp i32 ").append(rightTemp).append(" to double\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:double\n");
                rightTemp = tmp;
            }

            String bop = switch (op) {
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
                default -> throw new RuntimeException("Operador inválido para double: " + op);
            };

            llvm.append("  ").append(resultTemp).append(" = ").append(bop)
                    .append(" double ").append(leftTemp).append(", ").append(rightTemp).append("\n")
                    .append(";;VAL:").append(resultTemp).append(";;TYPE:")
                    .append(bop.startsWith("fcmp") ? "i1" : "double").append("\n");
            return llvm.toString();
        }

        if ((leftType.equals("%String*") || leftType.equals("i8*")) &&
                (rightType.equals("%String*") || rightType.equals("i8*"))) {

            // Normaliza para %String*
            if (leftType.equals("i8*")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = call %String* @createString(i8* ").append(leftTemp).append(")\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:%String*\n");
                leftTemp = tmp;
            }
            if (rightType.equals("i8*")) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = call %String* @createString(i8* ").append(rightTemp).append(")\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:%String*\n");
                rightTemp = tmp;
            }

            switch (op) {
                case "==" -> {
                    String tmp = temps.newTemp();
                    llvm.append("  ").append(tmp)
                            .append(" = call i1 @strcmp_eq(%String* ").append(leftTemp)
                            .append(", %String* ").append(rightTemp).append(")\n")
                            .append(";;VAL:").append(tmp).append(";;TYPE:i1\n");
                    return llvm.toString();
                }
                case "!=" -> {
                    String tmp = temps.newTemp();
                    llvm.append("  ").append(tmp)
                            .append(" = call i1 @strcmp_neq(%String* ").append(leftTemp)
                            .append(", %String* ").append(rightTemp).append(")\n")
                            .append(";;VAL:").append(tmp).append(";;TYPE:i1\n");
                    return llvm.toString();
                }
                case "+" -> {
                    String tmp = temps.newTemp();
                    llvm.append("  ").append(tmp)
                            .append(" = call %String* @concatStrings(%String* ").append(leftTemp)
                            .append(", %String* ").append(rightTemp).append(")\n")
                            .append(";;VAL:").append(tmp).append(";;TYPE:%String*\n");
                    return llvm.toString();
                }
                default -> throw new RuntimeException("Operador inválido para %String*: " + op);
            }
        }
        System.out.println("left type in AST "+ leftTypeAST);
        throw new RuntimeException("Tipos incompatíveis para operação: " + leftType + " " + op + " " + rightType);
    }

    private String emitLogicalShortCircuit(BinaryOpNode node, String op) {
        StringBuilder llvm = new StringBuilder();

        String leftLLVM = evaluateNode(node.left);
        llvm.append(leftLLVM).append("\n");

        String leftTemp = extractTemp(leftLLVM);
        String leftTypeAST = extractType(leftLLVM);
        String leftType = toLLVMType(leftTypeAST);

        CoercedBool lb = coerceToBool(leftTemp, leftType, llvm);

        String rhsLbl  = temps.newLabel(op.equals("&&") ? "and.rhs" : "or.rhs");
        String endLbl  = temps.newLabel(op.equals("&&") ? "and.end" : "or.end");
        String shortLbl = temps.newLabel(op.equals("&&") ? "and.short" : "or.short");
        String res = temps.newTemp();


        if (op.equals("&&")) {
            llvm.append("  br i1 ").append(lb.boolTemp)
                    .append(", label %").append(rhsLbl)
                    .append(", label %").append(shortLbl).append("\n");
        } else {
            llvm.append("  br i1 ").append(lb.boolTemp)
                    .append(", label %").append(shortLbl)
                    .append(", label %").append(rhsLbl).append("\n");
        }

        llvm.append(rhsLbl).append(":\n");
        String rightLLVM = evaluateNode(node.right);
        llvm.append(rightLLVM).append("\n");
        String rightTemp = extractTemp(rightLLVM);
        String rightTypeAST = extractType(rightLLVM);
        String rightType = toLLVMType(rightTypeAST);

        CoercedBool rb = coerceToBool(rightTemp, rightType, llvm);

        llvm.append("  br label %").append(endLbl).append("\n");

        llvm.append(shortLbl).append(":\n");

        String shortVal = temps.newTemp();
        if (op.equals("&&")) {
            llvm.append("  ").append(shortVal).append(" = add i1 0, 0\n");
        } else {
            llvm.append("  ").append(shortVal).append(" = add i1 1, 0\n");
        }
        llvm.append("  br label %").append(endLbl).append("\n");

        llvm.append(endLbl).append(":\n");
        llvm.append("  ").append(res).append(" = phi i1 ")
                .append("[ ").append(rb.boolTemp).append(", %").append(rhsLbl).append(" ], ")
                .append("[ ").append(shortVal).append(", %").append(shortLbl).append(" ]\n")
                .append(";;VAL:").append(res).append(";;TYPE:i1\n");

        return llvm.toString();
    }

    private CoercedBool coerceToBool(String valTemp, String llType, StringBuilder out) {
        // Já é i1
        if ("i1".equals(llType)) {
            return new CoercedBool(valTemp);
        }

        String tmp = temps.newTemp();
        switch (llType) {
            case "i32" -> {
                out.append("  ").append(tmp).append(" = icmp ne i32 ").append(valTemp).append(", 0\n");
            }
            case "double" -> {
                out.append("  ").append(tmp).append(" = fcmp one double ").append(valTemp).append(", 0.000000e+00\n");
            }
            case "%String*" -> {

                out.append("  ").append(tmp).append(" = icmp ne %String* ").append(valTemp).append(", null\n");
            }
            case "i8*" -> {
                out.append("  ").append(tmp).append(" = icmp ne i8* ").append(valTemp).append(", null\n");
            }
            default -> throw new RuntimeException("Não sei converter para boolean: " + llType);
        }
        out.append(";;VAL:").append(tmp).append(";;TYPE:i1\n");
        return new CoercedBool(tmp);
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
            case "List" -> "i8*";     // fallback genérico para lista opaca
            default -> type;           // já vem em %Struct*, %ArrayList*, i1, etc.
        };
    }

    private static class CoercedBool {
        final String boolTemp;
        CoercedBool(String t) { this.boolTemp = t; }
    }
}
