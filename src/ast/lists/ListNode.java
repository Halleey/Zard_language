package ast.lists;

import ast.ASTNode;
import expressions.DynamicList;
import expressions.TypedValue;

import java.util.Map;

public class ListNode extends ASTNode {
    private final DynamicList list;

    public ListNode(DynamicList list) {
        this.list = list;
    }

    public DynamicList getList() {
        return list;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        return  new TypedValue("list", list);
    }
}
