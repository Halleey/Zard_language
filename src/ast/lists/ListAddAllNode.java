package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class ListAddAllNode extends ASTNode {

    private final ASTNode targetListNode;
    private final List<ASTNode> args;

    public ListAddAllNode(ASTNode targetListNode, List<ASTNode> args) {
        this.targetListNode = targetListNode;
        this.args = args;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicList target = (DynamicList) targetListNode.evaluate(ctx).getValue();

        for (ASTNode argNode : args) {
            TypedValue val = argNode.evaluate(ctx);

            if (val.getType().equals("list")) {
                // Se for uma lista, adiciona cada elemento
                DynamicList other = (DynamicList) val.getValue();
                for (ASTNode elemNode : other.getElements()) {
                    target.add(elemNode.evaluate(ctx));
                }
            } else {
                // Se for valor simples, adiciona direto
                target.add(val);
            }
        }

        return new TypedValue("list", target);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ListAddAll:");
        System.out.println(prefix + "  Target List:");
        targetListNode.print(prefix + "    ");
        System.out.println(prefix + "  Arguments:");
        for (ASTNode arg : args) {
            arg.print(prefix + "    ");
        }
    }
}
