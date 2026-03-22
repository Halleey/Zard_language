package ast.expressions;

import ast.ASTNode;
import ast.variables.VariableNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;

public class UnaryOpNode extends ASTNode {

    private final String operator;
    private final ASTNode expr;

    private Type type;

    public String getOperator() {
        return operator;
    }

    public ASTNode getExpr() {
        return expr;
    }

    public UnaryOpNode(String operator, ASTNode expr) {
        this.operator = operator;
        this.expr = expr;
    }


    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        TypedValue val = expr.evaluate(ctx);
        Object value = val.value();
        Type t = val.type();

        if (operator.equals("++") || operator.equals("--")) {

            if (!(expr instanceof VariableNode varNode)) {
                throw new RuntimeException(
                        "Incremento/decremento só pode ser aplicado a variáveis");
            }

            if (t == PrimitiveTypes.INT) {

                int i = (Integer) value;
                int result = operator.equals("++") ? i + 1 : i - 1;

                TypedValue newVal =
                        new TypedValue(PrimitiveTypes.INT, result);

                ctx.setVariable(varNode.getName(), newVal);
                return newVal;
            }

            if (t == PrimitiveTypes.DOUBLE) {

                double d = (Double) value;
                double result = operator.equals("++") ? d + 1.0 : d - 1.0;

                TypedValue newVal =
                        new TypedValue(PrimitiveTypes.DOUBLE, result);

                ctx.setVariable(varNode.getName(), newVal);
                return newVal;
            }

            throw new RuntimeException(
                    "Incremento/decremento só válido para int ou double");
        }

        if (t == PrimitiveTypes.INT) {

            int i = (Integer) value;

            return new TypedValue(
                    PrimitiveTypes.INT,
                    operator.equals("-") ? -i : i
            );
        }

        if (t == PrimitiveTypes.DOUBLE) {

            double d = (Double) value;

            return new TypedValue(
                    PrimitiveTypes.DOUBLE,
                    operator.equals("-") ? -d : d
            );
        }

        if (t == PrimitiveTypes.FLOAT) {

            float f = (Float) value;

            return new TypedValue(
                    PrimitiveTypes.FLOAT,
                    operator.equals("-") ? -f : f
            );
        }

        throw new RuntimeException(
                "Operador unário '" + operator +
                        "' só é válido para tipos numéricos");
    }

    @Override
    public void bindChildren(StaticContext stx) {

        expr.setParent(this);
        expr.bind(stx);

        Type exprType = expr.getType();

        if (exprType == null) {
            throw new RuntimeException(
                    "Type resolution failed in UnaryOpNode (" + operator + ")");
        }

        switch (operator) {

            case "+", "-" -> {

                if (!exprType.isNumeric()) {
                    throw new RuntimeException(
                            "Unary '" + operator + "' requires numeric type but got " + exprType);
                }

                type = exprType;
            }

            case "++", "--" -> {

                if (!(expr instanceof VariableNode)) {
                    throw new RuntimeException(
                            "Increment/decrement only allowed on variables");
                }

                if (!(exprType == PrimitiveTypes.INT ||
                        exprType == PrimitiveTypes.DOUBLE ||
                        exprType == PrimitiveTypes.FLOAT)) {

                    throw new RuntimeException(
                            "Increment/decrement only allowed for numeric types");
                }

                type = exprType;
            }

            default -> throw new RuntimeException(
                    "Unknown unary operator: " + operator);
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public LLVMValue accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "UnaryOp: " + operator);
        expr.print(prefix + "  ");
    }
}