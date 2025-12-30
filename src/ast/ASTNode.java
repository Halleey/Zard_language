package ast;

import ast.context.runtime.RuntimeContext;
import ast.context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.Collections;
import java.util.List;

public abstract class ASTNode {
    public abstract String accept(LLVMEmitVisitor visitor);
    public abstract TypedValue evaluate(RuntimeContext ctx);
    public abstract void print(String prefix);
    public List<ASTNode> getChildren() {
        return Collections.emptyList(); // subclasses podem sobrescrever
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
