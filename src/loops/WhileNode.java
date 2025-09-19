package loops;

import ast.ASTNode;
import ast.exceptions.BreakLoop;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import low.LLVMEmitVisitor;

import java.util.List;
public class WhileNode extends ASTNode {
    public final ASTNode condition;
    public final List<ASTNode> body;

    public WhileNode(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        while (true) {
            TypedValue condVal = condition.evaluate(ctx);
            if (!(condVal.getValue() instanceof Boolean)) {
                throw new RuntimeException("Condição do while deve ser boolean");
            }
            if (!((Boolean) condVal.getValue())) break;

            try {
                for (ASTNode node : body) {
                    node.evaluate(ctx);
                }
            } catch (BreakLoop ignored) {
                break; // sai do while, mas continua o programa
            }
        }
        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "While:");
        System.out.println(prefix + "  Condition:");
        condition.print(prefix + "    ");
        System.out.println(prefix + "  Body:");
        for (ASTNode n : body) n.print(prefix + "    ");
    }
}
