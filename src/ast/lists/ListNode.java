package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class ListNode extends ASTNode {

    private final DynamicList list;

    public ListNode(DynamicList list) {
        this.list = list;
    }

    public DynamicList getList() {
        return list;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return new TypedValue("list", list);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "List:");
        List<ASTNode> elements = list.getElements(); // ASTNode mesmo

        if (elements.isEmpty()) {
            System.out.println(prefix + "  (vazia)");
        } else {
            for (int i = 0; i < elements.size(); i++) {
                TypedValue val = elements.get(i).evaluate(new RuntimeContext()); // avalia cada nó
                System.out.println(prefix + "  [" + i + "]: " + val.getValue() + " (" + val.getType() + ")");
            }
        }
    }

}
