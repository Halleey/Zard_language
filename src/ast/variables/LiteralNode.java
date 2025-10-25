package ast.variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class LiteralNode extends ASTNode {
    public final TypedValue value;

    public TypedValue getValue() {
        return value;
    }
    public String getType() {
        return value.type();
    }

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
        System.out.println(prefix + "Literal: (" + value.type() + ") " + value.value());
    }
}

