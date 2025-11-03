package ast;

import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;
public abstract class ASTNode {
    public abstract String accept(LLVMEmitVisitor visitor);
    public abstract TypedValue evaluate(RuntimeContext ctx);
    public abstract void print(String prefix);
}
