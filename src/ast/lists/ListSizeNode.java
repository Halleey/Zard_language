package ast.lists;

import ast.ASTNode;
import ast.context.RuntimeContext;
import ast.context.StaticContext;
import ast.expressions.TypedValue;
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
        // Avalia o ASTNode para obter o valor
        TypedValue listVal = nome.evaluate(ctx);

        if (!listVal.type().startsWith("List")) {
            throw new RuntimeException("O valor avaliado não é uma lista");
        }

        DynamicList dynamicList = (DynamicList) listVal.value();
        int size = dynamicList.size();

        return new TypedValue("int", size);
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
    public void bind(StaticContext stx) {

    }
}
