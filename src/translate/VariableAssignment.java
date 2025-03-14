package translate;

import expressions.Expression;
public class VariableAssignment extends Statement {
    public final String name;
    public final Expression value;

    public VariableAssignment(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(VariableTable table) {
        table.setVariable(name, value.evaluate(table)); // Armazena um TypedValue corretamente
    }

    @Override
    public String toString() {
        return "VariableAssignment{name='" + name + "', value=" + value + "}";
    }
}

