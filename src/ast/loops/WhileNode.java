package ast.loops;

import ast.ASTNode;
import ast.exceptions.BreakLoop;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class WhileNode extends ASTNode {
    public final ASTNode condition;
    public final List<ASTNode> body;

    public List<ASTNode> getBody() {
        return body;
    }

    public WhileNode(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        while (true) {
            TypedValue condVal = condition.evaluate(ctx);
            if (!(condVal.value() instanceof Boolean)) {
                throw new RuntimeException("Condição do while deve ser boolean");
            }
            if (!((Boolean) condVal.value())) break;

            // Cria um novo escopo para esta iteração
            RuntimeContext loopCtx = new RuntimeContext(ctx);

            try {
                for (ASTNode node : body) {
                    node.evaluate(loopCtx);
                }
            } catch (BreakLoop ignored) {
                break;
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
