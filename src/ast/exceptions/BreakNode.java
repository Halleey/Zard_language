package ast.exceptions;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import low.LLVMEmitVisitor;


public class BreakNode extends ASTNode {
    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        throw new BreakLoop(); // exceção para controlar break
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Break");
    }
}

