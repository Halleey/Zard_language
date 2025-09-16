package ast.lists;

import ast.ASTNode;
import expressions.DynamicList;
import expressions.TypedValue;

import java.util.Map;

public class ListClearNode extends ASTNode {


    private final ASTNode listNode;

    public ListClearNode(ASTNode listNode) {
        this.listNode = listNode;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        DynamicList list = (DynamicList) listNode.evaluate(variables).getValue();
        list.getElements().clear();
        return new TypedValue("list", list);
    }
}

