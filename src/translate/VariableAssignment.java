package translate;

import expressions.Expression;
import expressions.TypedValue;

public class VariableAssignment extends Statement {
    public final String name;
    public final Expression value;

    public VariableAssignment(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    public void execute(VariableTable table) {
        if (!table.hasVariable(name)) {
            throw new RuntimeException("Erro: Variável '" + name + "' não declarada.");
        }
        table.setVariable(name, new TypedValue(value.evaluate(table), table.getVariable(name).getType()));

    }

    @Override
    public String toString() {
        return "VariableAssignment{name='" + name + "', value=" + value + "}";
    }
}

