package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
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
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return new TypedValue("List", list);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "List:");
        List<ASTNode> elements = list.getElements();

        if (elements.isEmpty()) {
            System.out.println(prefix + "  (vazia)");
        } else {
            for (int i = 0; i < elements.size(); i++) {
                System.out.print(prefix + "  [" + i + "]: ");
                elements.get(i).print(prefix + "    ");
            }
        }
    }


}
