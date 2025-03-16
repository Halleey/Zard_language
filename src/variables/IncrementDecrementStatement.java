package variables;

import expressions.TypedValue;

public class IncrementDecrementStatement extends Statement {
    private final String variableName;
    private final String operator;

    public IncrementDecrementStatement(String variableName, String operator) {
        this.variableName = variableName;
        this.operator = operator;
    }

    @Override
    public void execute(VariableTable table) {
        if (!table.hasVariable(variableName)) {
            throw new RuntimeException("Erro: Variável '" + variableName + "' não declarada.");
        }

        TypedValue oldValue = table.getVariable(variableName);

        if (!oldValue.getType().equals("int")) {
            throw new RuntimeException("Erro: Operador '" + operator + "' só pode ser usado com inteiros.");
        }

        int newValue = (int) oldValue.getValue() + (operator.equals("++") ? 1 : -1);
        table.setVariable(variableName, new TypedValue(newValue, "int"));
    }

    @Override
    public String toString() {
        return "IncrementDecrementStatement{variable='" + variableName + "', operator='" + operator + "'}";
    }
}
