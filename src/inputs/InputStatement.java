package inputs;

import expressions.TypedValue;
import variables.Statement;
import variables.VariableTable;

import java.util.Scanner;

public class InputStatement extends Statement {
    private final String variableName;

    public InputStatement(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public void execute(VariableTable table) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o valor para " + variableName + ": ");
        String input = scanner.nextLine();

        // Tenta converter para o tipo correto baseado na variável existente
        TypedValue existingVar = table.getVariable(variableName);
        if (existingVar == null) {
            throw new RuntimeException("Erro: Variável '" + variableName + "' não foi declarada.");
        }

        Object convertedValue;
        String type = existingVar.getType();

        convertedValue = switch (type) {
            case "int" -> Integer.parseInt(input);
            case "double" -> Double.parseDouble(input);
            case "bool" -> Boolean.parseBoolean(input);
            default -> input;
        };

        table.setVariable(variableName, new TypedValue(convertedValue, type));
    }

    public String getVariableName() {
        return variableName;
    }
}
