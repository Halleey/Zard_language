package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.DynamicList;
import expressions.TypedValue;

import java.util.List;
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

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "List:");
        List<TypedValue> elements = list.getElements();
        if (elements.isEmpty()) {
            System.out.println(prefix + "  (vazia)");
        } else {
            for (int i = 0; i < elements.size(); i++) {
                TypedValue val = elements.get(i);
                System.out.println(prefix + "  [" + i + "]: " + val.getValue() + " (" + val.getType() + ")");
            }
        }
    }
}
