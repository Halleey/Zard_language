package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
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
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        ListValue list = (ListValue) listNode.evaluate(ctx).value();

        int index = ((Number) indexNode.evaluate(ctx).value()).intValue();

        return list.removeByIndex(index);
    }

    public ASTNode getIndexNode() {
        return indexNode;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListRemove:");
        System.out.println(prefix + "  List:");
        listNode.print(prefix + "    ");
        System.out.println(prefix + "  (Item removido)");
    }

    @Override
    public void bindChildren(StaticContext stx) {

    }

    public ASTNode getListNode() {
        return listNode;
    }
}
