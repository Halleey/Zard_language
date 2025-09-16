package loops;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.List;
import java.util.Map;
public class WhileNode extends ASTNode {
    public final ASTNode condition;
    public final List<ASTNode> body;

    public WhileNode(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        // Avalia a condição
        TypedValue condVal = condition.evaluate(variables);
        if (!(condVal.getValue() instanceof Boolean)) {
            throw new RuntimeException("Condição do while deve ser boolean");
        }

        // Enquanto a condição for verdadeira, executa o corpo
        while ((Boolean) condVal.getValue()) {
            for (ASTNode node : body) {
                node.evaluate(variables);
            }
            // Atualiza a condição após executar o corpo
            condVal = condition.evaluate(variables);
        }

        return null;
    }
}
