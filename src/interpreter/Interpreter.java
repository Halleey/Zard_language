package interpreter;

import expressions.Expression;
import expressions.LiteralExpression;
import expressions.TypedValue;
import translate.*;

public class Interpreter {
    private final VariableTable table;

    public Interpreter(VariableTable table) {
        this.table = table;
    }

    public void execute(VariableAssignment assignment) {
        if (!table.hasVariable(assignment.name)) {
            throw new RuntimeException("Variável '" + assignment.name + "' não foi declarada.");
        }
        Object evaluatedValue = evaluateExpression(assignment.value);
        TypedValue oldValue = table.getVariable(assignment.name);
        table.setVariable(assignment.name, new TypedValue(evaluatedValue, oldValue.getType()));
    }

    private Object evaluateExpression(Expression expr) {
        if (expr instanceof LiteralExpression) {
            return ((LiteralExpression) expr).token.getValue();
        } else if (expr instanceof VariableReference) {
            return table.getVariable(((VariableReference) expr).name).getValue();
        }
        throw new RuntimeException("Erro ao avaliar expressão.");
    }
}
