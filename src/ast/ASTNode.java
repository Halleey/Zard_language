package ast;

import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.Collections;
import java.util.List;
public abstract class ASTNode {
    
    public boolean isStatement() {
        return false;
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

    public void bind(StaticContext stx) {
        for (ASTNode child : getChildren()) {
            child.bind(stx);
        }
    }
}
