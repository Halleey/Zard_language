package expressions;

import variables.VariableTable;

public class IdentifierExpression extends Expression {
    public final String name;

    public IdentifierExpression(String name) {
        this.name = name;
    }

    @Override
    public TypedValue evaluate(VariableTable table) {
        TypedValue value = table.getVariable(name);
        if (value == null) {
            throw new RuntimeException("Variável não definida: " + name);
        }
        return value;
    }
}
