package variables;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.Map;

public class AssignmentNode extends ASTNode {
    public final String name;
    public final ASTNode valueNode;

    public AssignmentNode(String name, ASTNode valueNode) {
        this.name = name;
        this.valueNode = valueNode;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variável não declarada: " + name);
        }

        TypedValue value = valueNode.evaluate(variables);
        String existingType = variables.get(name).getType();

        if (!existingType.equals(value.getType())) {
            throw new RuntimeException("Erro de tipo: esperado " + existingType + " mas encontrado " + value.getType());
        }

        variables.put(name, value);
        return value;
    }
}