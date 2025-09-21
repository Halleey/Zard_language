package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;

public class ListSliceNode extends ASTNode {
    private final ASTNode listNode;
    private final ASTNode fromIndexNode;

    public ListSliceNode(ASTNode listNode, ASTNode fromIndexNode) {
        this.listNode = listNode;
        this.fromIndexNode = fromIndexNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue listVal = listNode.evaluate(ctx);
        if (!listVal.getType().equals("list")) {
            throw new RuntimeException("ListSlice aplicado a valor que não é lista");
        }

        DynamicList list = (DynamicList) listVal.getValue();
        int fromIndex = ((Number) fromIndexNode.evaluate(ctx).getValue()).intValue();

        List<ASTNode> slicedElements = new ArrayList<>();
        for (int i = fromIndex; i < list.getElements().size(); i++) {
            slicedElements.add(list.getElements().get(i));
        }

        return new TypedValue("list", new DynamicList(slicedElements));
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListSlice:");
        System.out.println(prefix + "  List:");
        listNode.print(prefix + "    ");
        System.out.println(prefix + "  From index:");
        fromIndexNode.print(prefix + "    ");
    }
}

