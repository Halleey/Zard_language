package ast.exceptions;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;


public class ReturnNode extends ASTNode {
    public final ASTNode expr;

    public ReturnNode(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {

        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue value = null;

        if (expr != null) {
            value = expr.evaluate(ctx);
        }

        throw new ReturnValue(value);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Return:");
        if (expr != null) {
            expr.print(prefix + "  ");
        }
    }

    @Override
    public void bindChildren(StaticContext stx) {
        if(expr != null) {
        expr.bind(stx);
        }
    }

    @Override
    public boolean isStatement() {
        return true;
    }

    public ASTNode getExpr() {
        return expr;
    }
}