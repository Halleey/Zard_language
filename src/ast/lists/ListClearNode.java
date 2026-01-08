package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
import low.module.LLVMEmitVisitor;

public class ListClearNode extends ASTNode {


    private final ASTNode listNode;

    public ListClearNode(ASTNode listNode) {
        this.listNode = listNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {

        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ListValue list = (ListValue) listNode.evaluate(ctx).value();

        list.clear();
        return new TypedValue("List<" + list.getElementType() + ">", list);
    }



    public ASTNode getListNode() {
        return listNode;
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
}

