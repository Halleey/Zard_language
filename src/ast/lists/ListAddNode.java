package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class ListAddNode extends ASTNode {

    private final ASTNode listNode;
    private final ASTNode valuesNode;

    public ListAddNode(ASTNode listNode, ASTNode valuesNode) {
        this.listNode = listNode;
        this.valuesNode = valuesNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {

        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicList list = (DynamicList) listNode.evaluate(ctx).getValue();
        TypedValue values = valuesNode.evaluate(ctx);
        list.add(values);
        return values;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListAdd:");
        System.out.println(prefix + "  List:");
        listNode.print(prefix + "    ");
        System.out.println(prefix + "  Value to Add:");
        valuesNode.print(prefix + "    ");
    }

    public ASTNode getListNode() {
        return listNode;
    }

    public ASTNode getValuesNode() {
        return valuesNode;
    }
}
