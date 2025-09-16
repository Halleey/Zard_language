package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
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
    public TypedValue evaluate(RuntimeContext ctx) {
        return new TypedValue("list", list);
    }
}
