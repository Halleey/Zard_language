package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;// Nó AST que representa uma lista tipada

public class ListNode extends ASTNode {

    private final DynamicList list;
    private final String type;

    public ListNode(DynamicList list) {
        this.list = list;
        this.type = "List<" + list.getElementType() + ">";
    }

    public DynamicList getList() {
        return list;
    }

    public String getType() {
        return type;
    }

    @Override
    public void bind(StaticContext stx) {

    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return new TypedValue(type, list);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + type + ":");

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
