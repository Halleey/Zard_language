package ast.exceptions;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;


public class BreakNode extends ASTNode {
    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        throw new BreakLoop(); // exceção para controlar break
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Break");
    }

    @Override
    public void bindChildren(StaticContext stx) {

    }
}

