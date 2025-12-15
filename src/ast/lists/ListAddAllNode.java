package ast.lists;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.variables.ListValue;
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
        ListValue target = (ListValue) targetListNode.evaluate(ctx).value();

        for (ASTNode argNode : args) {
            TypedValue val = argNode.evaluate(ctx);

            if (val.type().equals("List")) {
                // Se for uma lista, adiciona cada elemento
                ListValue other = (ListValue) val.value();
                for (TypedValue elem : other.getElements()) {
                    target.add(elem);
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
}
