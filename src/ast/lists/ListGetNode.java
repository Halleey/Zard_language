package ast.lists;

import ast.ASTNode;
import ast.context.RuntimeContext;
import ast.context.StaticContext;
import ast.expressions.TypedValue;
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
        DynamicList list = (DynamicList) listName.evaluate(ctx).value();
        int index = ((Number) indexNode.evaluate(ctx).value()).intValue();
        return list.get(index, ctx);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListGet:");
        listName.print(prefix + "  ");
        indexNode.print(prefix + "  ");
    }

    @Override
    public void bind(StaticContext stx) {

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
