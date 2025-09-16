package ast.exceptions;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;


public class ReturnNode extends ASTNode {
    public final ASTNode expr;

    public ReturnNode(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue value = expr.evaluate(ctx);
        throw new ReturnValue(value); // exceção para controlar return
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Return:");
        if (expr != null) {
            expr.print(prefix + "  ");
        }
    }
}
