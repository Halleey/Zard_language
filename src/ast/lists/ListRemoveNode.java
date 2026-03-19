package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
import context.statics.symbols.ListType;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;

public class ListRemoveNode extends ASTNode {

    private final ASTNode listNode;
    private final ASTNode indexNode;

    public ListRemoveNode(ASTNode listNode, ASTNode indexNode) {
        this.listNode = listNode;
        this.indexNode = indexNode;
    }

    @Override
    public LLVMValue accept(LLVMEmitVisitor visitor) {
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

        listNode.setParent(this);
        listNode.bind(stx);

        indexNode.setParent(this);
        indexNode.bind(stx);

        Type listType = listNode.getType();

        if (listType == null)
            throw new RuntimeException("ListRemove: list type is null");

        if (!(listType instanceof ListType))
            throw new RuntimeException(
                    "ListRemove applied in non-list type:: " + listType.name());
    }
    public ASTNode getListNode() {
        return listNode;
    }
}
