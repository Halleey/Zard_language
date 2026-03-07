package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
import context.statics.symbols.ListType;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class ListGetNode extends ASTNode {

    private final ASTNode listExpr;
    private final ASTNode indexNode;

    private Type elementType;

    public ListGetNode(ASTNode listExpr, ASTNode indexNode) {
        this.listExpr = listExpr;
        this.indexNode = indexNode;
    }

    public ListGetNode(ASTNode listExpr, ASTNode indexNode, Type elementType) {
        this.listExpr = listExpr;
        this.indexNode = indexNode;
        this.elementType = elementType;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        ListValue list = (ListValue) listExpr.evaluate(ctx).value();

        int index = ((Number) indexNode.evaluate(ctx).value()).intValue();

        return list.get(index);
    }

    @Override
    public void bindChildren(StaticContext ctx) {

        listExpr.bind(ctx);
        indexNode.bind(ctx);

        Type listType = listExpr.getType();

        if (!(listType instanceof ListType lt)) {
            throw new RuntimeException("Expressão não é uma lista: " + listType);
        }

        elementType = lt.elementType();
    }

    @Override
    public Type getType() {
        return elementType;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListGet:");
        listExpr.print(prefix + "  ");
        indexNode.print(prefix + "  ");
    }

    @Override
    public List<ASTNode> getChildren() {
        return List.of(listExpr, indexNode);
    }

    public ASTNode getListName() {
        return listExpr;
    }

    public ASTNode getIndexNode() {
        return indexNode;
    }

    public Type getElementType() {
        return elementType;
    }
}