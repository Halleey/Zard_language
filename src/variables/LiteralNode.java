package variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class LiteralNode extends ASTNode {
    public final TypedValue value;

    public LiteralNode(TypedValue value) {
        this.value = value;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return value;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Literal: (" + value.getType() + ") " + value.getValue());
    }

}

