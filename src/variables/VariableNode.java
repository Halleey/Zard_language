package variables;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.Map;

public class VariableNode extends ASTNode {
    public final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variável não definida: " + name);
        }
        return variables.get(name);
    }
}