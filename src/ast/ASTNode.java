package ast;

import ast.runtime.RuntimeContext;
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
}
