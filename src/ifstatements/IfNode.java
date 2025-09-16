package ifstatements;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

import java.util.List;
import java.util.Map;
import java.util.List;

public class IfNode extends ASTNode {
    public final ASTNode condition;
    public final List<ASTNode> thenBranch;
    public final List<ASTNode> elseBranch; // pode ser null

    public IfNode(ASTNode condition, List<ASTNode> thenBranch, List<ASTNode> elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue condVal = condition.evaluate(ctx);

        if (!(condVal.getValue() instanceof Boolean)) {
            throw new RuntimeException("Condição do if deve ser boolean");
        }

        if ((Boolean) condVal.getValue()) {
            for (ASTNode node : thenBranch) {
                node.evaluate(ctx);
            }
        } else if (elseBranch != null) {
            for (ASTNode node : elseBranch) {
                node.evaluate(ctx);
            }
        }

        return null;
    }
}
