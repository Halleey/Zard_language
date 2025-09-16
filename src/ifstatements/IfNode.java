package ifstatements;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.List;
import java.util.Map;

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
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        TypedValue condVal = condition.evaluate(variables);

        if (!(condVal.getValue() instanceof Boolean)) {
            throw new RuntimeException("Condição do if deve ser boolean");
        }

        if ((Boolean) condVal.getValue()) {
            for (ASTNode node : thenBranch) {
                node.evaluate(variables);
            }
        } else if (elseBranch != null) {
            for (ASTNode node : elseBranch) {
                node.evaluate(variables);
            }
        }

        return null;
    }
}
