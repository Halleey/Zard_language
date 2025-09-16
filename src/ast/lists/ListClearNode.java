package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.DynamicList;
import expressions.TypedValue;

import java.util.Map;

public class ListClearNode extends ASTNode {


    private final ASTNode listNode;

    public ListClearNode(ASTNode listNode) {
        this.listNode = listNode;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicList list = (DynamicList) listNode.evaluate(ctx).getValue();
        list.getElements().clear();
        return new TypedValue("list", list);
    }
}

