package ast.variables;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;

public class LiteralNode extends ASTNode {
    public final TypedValue value;

    public TypedValue getValue() {
        return value;
    }
    @Override
    public Type getType() {
        return value.type();
    }

    @Override
    public void bindChildren(StaticContext stx) {

    }

    public LiteralNode(TypedValue value) {
        this.value = value;
    }

    @Override
    public LLVMValue accept(LLVMEmitVisitor visitor) {
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

