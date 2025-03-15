package interpreter;

import expressions.BinaryExpression;
import expressions.Expression;
import expressions.LiteralExpression;
import expressions.TypedValue;
import variables.*;

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
            return ((LiteralExpression) expr).evaluate(table).getValue();
        } else if (expr instanceof VariableReference) {
            return table.getVariable(((VariableReference) expr).name).getValue();
        } else if (expr instanceof BinaryExpression) {
            return ((BinaryExpression) expr).evaluate(table).getValue();
        }
        throw new RuntimeException("Erro ao avaliar expressão.");
    }

}
