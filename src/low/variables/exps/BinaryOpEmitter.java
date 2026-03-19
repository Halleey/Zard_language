package low.variables.exps;
import ast.ASTNode;
import ast.variables.VariableNode;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.expressions.BinaryOpNode;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.*;


public class BinaryOpEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;

    public BinaryOpEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public LLVMValue emit(BinaryOpNode node) {
        String op = node.operator;
        StringBuilder llvm = new StringBuilder();

        // Avalia os nós filhos e obtém LLVMValues (name + tipo)
        LLVMValue leftVal = evaluateNode(node.left);
        LLVMValue rightVal = evaluateNode(node.right);

        llvm.append(leftVal.getCode())
                .append("\n")
                .append(rightVal.getCode())
                .append("\n");

        LLVMTYPES leftTypeLLVM = leftVal.getType();
        LLVMTYPES rightTypeLLVM = rightVal.getType();
        String leftTemp = leftVal.getName();
        String rightTemp = rightVal.getName();

        // Short-circuit && e ||
        if ("&&".equals(op) || "||".equals(op)) {
            return emitLogicalShortCircuit(leftVal, rightVal, op);
        }

        String resultTemp = temps.newTemp();

        // Booleanos
        if (leftTypeLLVM instanceof LLVMBool && rightTypeLLVM instanceof LLVMBool) {
            String bop = switch (op) {
                case "==" -> "icmp eq";
                case "!=" -> "icmp ne";
                default -> throw new RuntimeException("Operador inválido para boolean: " + op);
            };
            llvm.append("  ").append(resultTemp).append(" = ").append(bop)
                    .append(" i1 ").append(leftTemp).append(", ").append(rightTemp).append("\n");
            return new LLVMValue(new LLVMBool(), resultTemp, llvm.toString());
        }

        // Char
        if (leftTypeLLVM instanceof LLVMChar && rightTypeLLVM instanceof LLVMChar) {
            String bop = switch (op) {
                case "==" -> "icmp eq";
                case "!=" -> "icmp ne";
                case ">" -> "icmp sgt";
                case "<" -> "icmp slt";
                case ">=" -> "icmp sge";
                case "<=" -> "icmp sle";
                default -> throw new RuntimeException("Operador inválido para char: " + op);
            };
            llvm.append("  ").append(resultTemp).append(" = ").append(bop)
                    .append(" i8 ").append(leftTemp).append(", ").append(rightTemp).append("\n");
            return new LLVMValue(new LLVMBool(), resultTemp, llvm.toString());
        }

        // Inteiros
        if (leftTypeLLVM instanceof LLVMInt && rightTypeLLVM instanceof LLVMInt) {
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
                    .append(" i32 ").append(leftTemp).append(", ").append(rightTemp).append("\n");
            LLVMTYPES resultType = bop.startsWith("icmp") ? new LLVMBool() : new LLVMInt();
            return new LLVMValue(resultType, resultTemp, llvm.toString());
        }

        // Float
        if (leftTypeLLVM instanceof LLVMFloat && rightTypeLLVM instanceof LLVMFloat) {
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
                default -> throw new RuntimeException("Operador inválido para float: " + op);
            };
            llvm.append("  ").append(resultTemp).append(" = ").append(bop)
                    .append(" float ").append(leftTemp).append(", ").append(rightTemp).append("\n");
            LLVMTYPES resultType = bop.startsWith("fcmp") ? new LLVMBool() : new LLVMFloat();
            return new LLVMValue(resultType, resultTemp, llvm.toString());
        }

        // Double (incluindo conversões automáticas)
        if (leftTypeLLVM instanceof LLVMDouble || rightTypeLLVM instanceof LLVMDouble) {
            // Int para double
            if (leftTypeLLVM instanceof LLVMInt) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = sitofp i32 ").append(leftTemp).append(" to double\n");
                leftTemp = tmp;
            }
            if (rightTypeLLVM instanceof LLVMInt) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = sitofp i32 ").append(rightTemp).append(" to double\n");
                rightTemp = tmp;
            }

            // Float para double
            if (leftTypeLLVM instanceof LLVMFloat) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = fpext float ").append(leftTemp).append(" to double\n");
                leftTemp = tmp;
            }
            if (rightTypeLLVM instanceof LLVMFloat) {
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = fpext float ").append(rightTemp).append(" to double\n");
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
                    .append(" double ").append(leftTemp).append(", ").append(rightTemp).append("\n");
            LLVMTYPES resultType = bop.startsWith("fcmp") ? new LLVMBool() : new LLVMDouble();
            return new LLVMValue(resultType, resultTemp, llvm.toString());
        }

        // Strings
        if ((leftTypeLLVM instanceof LLVMString) && (rightTypeLLVM instanceof LLVMString)) {
            switch (op) {
                case "==" -> {
                    String cmp = temps.newTemp();
                    String res = temps.newTemp();
                    llvm.append("  ").append(cmp).append(" = call i32 @compareString(%String* ")
                            .append(leftTemp).append(", %String* ").append(rightTemp).append(")\n");
                    llvm.append("  ").append(res).append(" = icmp eq i32 ").append(cmp).append(", 1\n");
                    return new LLVMValue(new LLVMBool(), res, llvm.toString());
                }
                case "!=" -> {
                    String cmp = temps.newTemp();
                    String res = temps.newTemp();
                    llvm.append("  ").append(cmp).append(" = call i32 @compareString(%String* ")
                            .append(leftTemp).append(", %String* ").append(rightTemp).append(")\n");
                    llvm.append("  ").append(res).append(" = icmp eq i32 ").append(cmp).append(", 0\n");
                    return new LLVMValue(new LLVMBool(), res, llvm.toString());
                }
                case "+" -> {
                    String tmp = temps.newTemp();
                    llvm.append("  ").append(tmp).append(" = call %String* @concatStrings(%String* ")
                            .append(leftTemp).append(", %String* ").append(rightTemp).append(")\n");
                    return new LLVMValue(new LLVMString(), tmp, llvm.toString());
                }
                default -> throw new RuntimeException("Operador inválido para String: " + op);
            }
        }

        throw new RuntimeException(
                "Tipos incompatíveis para operação: " + leftTypeLLVM + " " + op + " " + rightTypeLLVM
        );
    }

    private LLVMValue emitLogicalShortCircuit(LLVMValue left, LLVMValue right, String op) {
        StringBuilder llvm = new StringBuilder();

        // Coerce i32, double, %String* para i1
        CoercedBool lb = coerceToBool(left, llvm);
        String rhsLbl  = temps.newLabel(op.equals("&&") ? "and.rhs" : "or.rhs");
        String endLbl  = temps.newLabel(op.equals("&&") ? "and.end" : "or.end");
        String shortLbl = temps.newLabel(op.equals("&&") ? "and.short" : "or.short");

        String resTemp = temps.newTemp(); // SSA do resultado

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
        CoercedBool rb = coerceToBool(right, llvm);
        llvm.append("  br label %").append(endLbl).append("\n");

        llvm.append(shortLbl).append(":\n");
        String shortVal = temps.newTemp();
        llvm.append("  ").append(shortVal).append(" = add i1 0, ").append(op.equals("&&") ? 0 : 1).append("\n");
        llvm.append("  br label %").append(endLbl).append("\n");

        llvm.append(endLbl).append(":\n");
        llvm.append("  ").append(resTemp).append(" = phi i1 ")
                .append("[ ").append(rb.boolTemp).append(", %").append(rhsLbl).append(" ], ")
                .append("[ ").append(shortVal).append(", %").append(shortLbl).append(" ]\n");

        return new LLVMValue(new LLVMBool(), resTemp, llvm.toString());
    }

    private CoercedBool coerceToBool(LLVMValue val, StringBuilder out) {
        String tmp = temps.newTemp();
        LLVMTYPES llType = val.getType();
        String temp = val.getName();

        if (llType instanceof LLVMInt) {
            out.append("  ").append(tmp).append(" = icmp ne i32 ").append(temp).append(", 0\n");
        } else if (llType instanceof LLVMDouble) {
            out.append("  ").append(tmp).append(" = fcmp one double ").append(temp).append(", 0.0\n");
        } else if (llType instanceof LLVMString) {
            out.append("  ").append(tmp).append(" = icmp ne %String* ").append(temp).append(", null\n");
            //melhorar isso
        } else if (llType instanceof LLVMPointer) { // por ex: i8*, mas isso aqui preciso de testes no futuro
            out.append("  ").append(tmp).append(" = icmp ne i8* ").append(temp).append(", null\n");
        } else if (llType instanceof LLVMBool) {
            return new CoercedBool(temp); // já é i1
        } else {
            throw new RuntimeException("Não sei converter para boolean: " + llType);
        }

        return new CoercedBool(tmp);
    }

    private LLVMValue evaluateNode(ASTNode node) {
        if (node instanceof VariableNode varNode) {
            return visitor.getVariableEmitter().emitLoad(varNode.getName());
        }
        return node.accept(visitor);
    }

    private static class CoercedBool {
        final String boolTemp;
        CoercedBool(String t) { this.boolTemp = t; }
    }
}