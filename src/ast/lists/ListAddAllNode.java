package ast.lists;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.list.ListValue;
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

    public TypedValue evaluate(RuntimeContext ctx) {

        ListValue target = (ListValue) targetListNode.evaluate(ctx).value();

        for (ASTNode arg : args) {
            TypedValue tv = arg.evaluate(ctx);

            if (tv.value() instanceof ListValue other) {
                for (int i = 0; i < other.size(); i++) {
                    target.add(other.get(i));
                }
            } else {
                target.add(tv);
            }
        }

        return new TypedValue("List<" + target.getElementType() + ">", target);
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
    public void bindChildren(StaticContext stx) {

    }
}
