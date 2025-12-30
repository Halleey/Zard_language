package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class ListAddAllNode extends ASTNode {

    private final ASTNode targetListNode;
    private final List<ASTNode> args;

    public ASTNode getTargetListNode() {
        return targetListNode;
    }

    public List<ASTNode> getArgs() {
        return args;
    }

    public ListAddAllNode(ASTNode targetListNode, List<ASTNode> args) {
        this.targetListNode = targetListNode;
        this.args = args;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicList target = (DynamicList) targetListNode.evaluate(ctx).value();

        for (ASTNode argNode : args) {
            TypedValue val = argNode.evaluate(ctx);

            if (val.type().equals("List")) {
                // Se for uma lista, adiciona cada elemento
                DynamicList other = (DynamicList) val.value();
                for (ASTNode elemNode : other.getElements()) {
                    target.add(elemNode.evaluate(ctx));
                }
            } else {
                // Se for valor simples, adiciona direto
                target.add(val);
            }
        }

        return new TypedValue("List", target);
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

    @Override
    public void bind(StaticContext stx) {

    }
}
