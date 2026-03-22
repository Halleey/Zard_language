package ast.expressions;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;

import java.util.List;

public class ExpressionStatementNode extends ASTNode {

    private final ASTNode expr;

    public ExpressionStatementNode(ASTNode expr) {
        this.expr = expr;
    }

    public ASTNode getExpr() {
        return expr;
    }

    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public LLVMValue accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return expr.evaluate(ctx);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ExpressionStatement:");
        expr.print(prefix + "  ");
    }

    @Override
    public List<ASTNode> getChildren() {
        return List.of(expr);
    }
}