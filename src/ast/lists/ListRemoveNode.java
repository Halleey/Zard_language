package ast.lists;

import ast.ASTNode;
import expressions.DynamicList;
import expressions.TypedValue;

import java.util.Map;

public class ListRemoveNode extends ASTNode {

    private final ASTNode listNode;
    private final ASTNode indexNode;

    public ListRemoveNode(ASTNode listNode, ASTNode indexNode) {
        this.listNode = listNode;
        this.indexNode = indexNode;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        DynamicList list = (DynamicList) listNode.evaluate(variables).getValue();
        int index = ((Number) indexNode.evaluate(variables).getValue()).intValue();
        TypedValue removed = list.get(index);
        list.getElements().remove(index);
        return removed;
    }

    public ASTNode getListNode() {
        return listNode;
    }
}