package ast;

import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.Collections;
import java.util.List;


public abstract class ASTNode {

    private StaticContext staticContext;

    public boolean isStatement() {
        return false;
    }

    public final void bind(StaticContext ctx) {
        this.staticContext = ctx;
        bindChildren(ctx);
    }

    protected void bindChildren(StaticContext ctx) {
        for (ASTNode child : getChildren()) {
            if (child != null) {
                child.setParent(this);
                child.bind(ctx);
            }
        }
    }


    private ASTNode parent;
    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }


    public StaticContext getStaticContext() {
        return staticContext;
    }

    public abstract String accept(LLVMEmitVisitor visitor);
    public abstract TypedValue evaluate(RuntimeContext ctx);
    public abstract void print(String prefix);

    public List<ASTNode> getChildren() {
        return Collections.emptyList();
    }

    public String getType() {
        return null;
    }
}
