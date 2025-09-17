package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.DynamicList;
import expressions.TypedValue;

import java.util.List;


public class ListAddAllNode extends ASTNode {

    private final ASTNode targetListNode;
    private final ASTNode sourceListNode;

    public ListAddAllNode(ASTNode targetListNode, ASTNode sourceListNode) {
        this.targetListNode = targetListNode;
        this.sourceListNode = sourceListNode;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicList target = (DynamicList) targetListNode.evaluate(ctx).getValue();
        List<TypedValue> sourceValues = ((DynamicList) sourceListNode.evaluate(ctx).getValue()).evaluate(ctx);

        // Adiciona cada elemento da source na target
        for (TypedValue item : sourceValues) {
            target.add(item);
        }
        return new TypedValue("list", target);
    }


    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListAddAll:");
        System.out.println(prefix + "  Target List:");
        targetListNode.print(prefix + "    ");
        System.out.println(prefix + "  Source List:");
        sourceListNode.print(prefix + "    ");
    }

    public ASTNode getTargetListNode() {
        return targetListNode;
    }

    public ASTNode getSourceListNode() {
        return sourceListNode;
    }
}
