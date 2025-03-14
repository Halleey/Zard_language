package translate;

import expressions.Expression;
import expressions.LiteralExpression;
import expressions.TypedValue;
import tokens.Token;

public class VariableDeclaration extends Statement {
    public final Token type;
    public final String name;
    public final Expression value;

    public VariableDeclaration(Token type, String name, Expression value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public void execute(VariableTable table) {
        Object evaluatedValue = evaluateExpression(value);
        table.setVariable(name, new TypedValue(evaluatedValue, table.getVariable(name).getType()));
    }

    private Object evaluateExpression(Expression expr) {
        if (expr instanceof LiteralExpression) {
            return ((LiteralExpression) expr).token.getValue();
        }
        throw new RuntimeException("Erro ao avaliar expressão.");
    }
}