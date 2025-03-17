package variables;

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

        TypedValue evaluatedValue = value.evaluate(table);
        TypedValue oldValue = table.getVariable(name);


        // Converter o valor para o tipo da variável
        Object convertedValue = convertToType(evaluatedValue.getValue(), oldValue.getType());

        table.setVariable(name, new TypedValue(convertedValue, oldValue.getType()));
    }

    private Object convertToType(Object value, String type) {
        if (value instanceof TypedValue) {
            value = ((TypedValue) value).getValue();
        }

        return switch (type) {
            case "int" -> {
                if (value instanceof Number) {
                    yield ((Number) value).intValue(); // Converte corretamente números para int
                }
                yield Integer.parseInt(value.toString()); // Caso seja string
            }
            case "double" -> {
                if (value instanceof Number) {
                    yield ((Number) value).doubleValue(); // Garante que double seja convertido corretamente
                }
                yield Double.parseDouble(value.toString());
            }
            case "bool" -> {
                if (value instanceof Boolean) {
                    yield value;
                }
                if (value instanceof Number) {
                    yield ((Number) value).intValue() != 0; // Converte 0 para false e qualquer outro número para true
                }
                yield Boolean.parseBoolean(value.toString());
            }
            case "string" -> value.toString();
            default -> throw new RuntimeException("Tipo desconhecido: " + type);
        };
    }

    @Override
    public String toString() {
        return "VariableAssignment{name='" + name + "', value=" + value + "}";
    }
}