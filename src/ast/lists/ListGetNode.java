package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class ListGetNode extends ASTNode {

    private final ASTNode listName;
    private final ASTNode indexNode;

    public ListGetNode(ASTNode listName, ASTNode indexNode) {
        this.listName = listName;
        this.indexNode = indexNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicList list = (DynamicList) listName.evaluate(ctx).getValue();
        int index = ((Number) indexNode.evaluate(ctx).getValue()).intValue();
        return list.get(index, ctx);
    }

    @Override
    public void print(String prefix) {

    }
}
