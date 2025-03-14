package variables;

import expressions.Expression;
import expressions.LiteralExpression;
import expressions.TypedValue;
import tokens.Token;


public class VariableDeclaration extends Statement {
    private final Token type;
    private final String name;
    private final Expression value;

    public VariableDeclaration(Token type, String name, Expression value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public void execute(VariableTable table) {
        Object evaluatedValue = (value != null) ? evaluateExpression(value) : getDefaultValue();
        table.setVariable(name, new TypedValue(evaluatedValue, type.getValue())); // Define o tipo corretamente
    }

    private Object evaluateExpression(Expression expr) {
        if (expr instanceof LiteralExpression) {
            return ((LiteralExpression) expr).token.getValue();
        }
        throw new RuntimeException("Erro ao avaliar expressão.");
    }

    private Object getDefaultValue() {
        return switch (type.getValue()) {
            case "int" -> 0;
            case "double" -> 0.0;
            case "string" -> "";
            case "bool" -> false;
            default -> null; // Outros tipos podem ser nulos
        };
    }

    @Override
    public String toString() {
        return "VariableDeclaration{name='" + name + "', type='" + type.getValue() + "', value=" + value + "}";
    }
}
