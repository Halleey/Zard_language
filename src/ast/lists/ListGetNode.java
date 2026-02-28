package ast.lists;

import ast.ASTNode;
import ast.variables.VariableNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.Symbol;
import context.statics.list.ListValue;
import low.module.LLVMEmitVisitor;

public class ListGetNode extends ASTNode {

    private final ASTNode listName;
    private final ASTNode indexNode;
    private  String elementType;

    public ListGetNode(ASTNode listName, ASTNode indexNode, String elementType) {
        this.listName = listName;
        this.indexNode = indexNode;
        this.elementType = elementType;
    }

    public ListGetNode(ASTNode listName, ASTNode indexNode) {
        this.listName = listName;
        this.indexNode = indexNode;
    }



    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        ListValue list = (ListValue) listName.evaluate(ctx).value();

        int index = ((Number) indexNode.evaluate(ctx).value()).intValue();

        return list.get(index);
    }
    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListGet:");
        listName.print(prefix + "  ");
        indexNode.print(prefix + "  ");
    }
    @Override
    public String getType() {
        return elementType;
    }



    @Override
    public void bindChildren(StaticContext stx) {

        listName.bind(stx);
        indexNode.bind(stx);

        String listType = listName.getType();

        if (listType == null || !listType.startsWith("List<")) {
            throw new RuntimeException("Expressão não é uma lista");
        }

        this.elementType = listType.substring(5, listType.length() - 1);
    }

    public ASTNode getListName() {
        return listName;
    }

    public ASTNode getIndexNode() {
        return indexNode;
    }

    public String getElementType() {
        return elementType;
    }
}
