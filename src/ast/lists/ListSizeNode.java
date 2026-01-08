package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
import low.module.LLVMEmitVisitor;

public class ListSizeNode extends ASTNode {

    private final ASTNode nome; // pode ser variável ou expressão que retorne lista

    public ListSizeNode(ASTNode nome) {
        this.nome = nome;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        ListValue list = (ListValue)nome.evaluate(ctx).value();

        return new TypedValue("int", list.size());
    }

    public ASTNode getNome() {
        return nome;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListSize:");
        nome.print(prefix + "  ");
    }

    @Override
    public void bindChildren(StaticContext stx) {

    }
}
