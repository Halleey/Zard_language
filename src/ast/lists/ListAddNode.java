package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.DynamicList;
import expressions.TypedValue;

import java.util.Map;
public class ListAddNode extends ASTNode {

    private final ASTNode listNode;
    private final ASTNode valuesNode;

    public ListAddNode(ASTNode listNode, ASTNode valuesNode) {
        this.listNode = listNode;
        this.valuesNode = valuesNode;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicList list = (DynamicList) listNode.evaluate(ctx).getValue();
        TypedValue values = valuesNode.evaluate(ctx);
        list.add(values);
        return values;
    }

    public ASTNode getListNode() {
        return listNode;
    }

    public ASTNode getValuesNode() {
        return valuesNode;
    }
}
