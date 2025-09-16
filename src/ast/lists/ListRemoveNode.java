package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
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
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicList list = (DynamicList) listNode.evaluate(ctx).getValue();
        int index = ((Number) indexNode.evaluate(ctx).getValue()).intValue();
        TypedValue removed = list.get(index);
        list.getElements().remove(index);
        return removed;
    }

    public ASTNode getListNode() {
        return listNode;
    }
}
