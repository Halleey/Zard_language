package ast.variables;

import ast.ASTNode;
import ast.context.runtime.RuntimeContext;
import ast.context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class UnaryOpNode extends ASTNode {
    private final String operator;
    private final ASTNode expr; // pode ser VariableNode ou qualquer expressão

    public UnaryOpNode(String operator, ASTNode expr) {
        this.operator = operator;
        this.expr = expr;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue val = expr.evaluate(ctx);
        Object value = val.value();

        // Suporte a incremento/decremento
        if (operator.equals("++") || operator.equals("--")) {
            if (!(expr instanceof VariableNode varNode)) {
                throw new RuntimeException("Incremento/decremento só pode ser aplicado a variáveis");
            }

            if (value instanceof Integer i) {
                int result = operator.equals("++") ? i + 1 : i - 1;
                TypedValue newVal = new TypedValue("int", result);
                ctx.setVariable(varNode.getName(), newVal);
                return newVal;
            }

            if (value instanceof Double d) {
                double result = operator.equals("++") ? d + 1.0 : d - 1.0;
                TypedValue newVal = new TypedValue("double", result);
                ctx.setVariable(varNode.getName(), newVal);
                return newVal;
            }

            throw new RuntimeException("Incremento/decremento só é válido para int ou double");
        }

        // Suporte a operador unário + ou -
        if (value instanceof Integer i) {
            return new TypedValue("int", operator.equals("-") ? -i : i);
        }
        if (value instanceof Double d) {
            return new TypedValue("double", operator.equals("-") ? -d : d);
        }

        throw new RuntimeException("Operador unário '" + operator + "' só é válido para int ou double");
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "UnaryOp: " + operator);
        expr.print(prefix + "  ");
    }

    @Override
    public void bind(StaticContext stx) {

    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getExpr() {
        return expr;
    }
}
