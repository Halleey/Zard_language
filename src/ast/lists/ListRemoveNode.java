package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class ListRemoveNode extends ASTNode {



    private final ASTNode listNode;
    private final ASTNode indexNode;

    public ListRemoveNode(ASTNode listNode, ASTNode indexNode) {
        this.listNode = listNode;
        this.indexNode = indexNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override


    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicList list = (DynamicList) listNode.evaluate(ctx).getValue();
        int index = ((Number) indexNode.evaluate(ctx).getValue()).intValue();
        return list.removeByIndex(index, ctx); // usa método novo
    }



    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListRemove:");
        System.out.println(prefix + "  List:");
        listNode.print(prefix + "    ");
        System.out.println(prefix + "  (Item removido)");
    }

    public ASTNode getListNode() {
        return listNode;
    }
}
