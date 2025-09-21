package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class ListSizeNode extends ASTNode {

    private final ASTNode nome; // pode ser variável ou expressão que retorne lista

    public ListSizeNode(ASTNode nome) {
        this.nome = nome;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        // Avalia o ASTNode para obter o valor
        TypedValue listVal = nome.evaluate(ctx);

        if (!listVal.getType().equals("list")) {
            throw new RuntimeException("O valor avaliado não é uma lista");
        }

        DynamicList dynamicList = (DynamicList) listVal.getValue();
        int size = dynamicList.size();

        return new TypedValue("int", size);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListSize:");
        nome.print(prefix + "  ");
    }
}
