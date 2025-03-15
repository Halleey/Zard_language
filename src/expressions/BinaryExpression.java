package expressions;


import tokens.Token;
import variables.VariableTable;

public class BinaryExpression extends Expression {
    public final Expression left;
    public final Token operator;
    public final Expression right;

    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public TypedValue evaluate(VariableTable table) {
        TypedValue leftValue = left.evaluate(table);
        TypedValue rightValue = right.evaluate(table);
        String op = operator.getValue();

        // Concatenação de strings
        if (op.equals("+") && (leftValue.getType().equals("string") || rightValue.getType().equals("string"))) {
            return new TypedValue(leftValue.getValue().toString() + rightValue.getValue().toString(), "string");
        }

        // Operações matemáticas
        if (leftValue.getValue() instanceof Number && rightValue.getValue() instanceof Number) {
            double leftNum = ((Number) leftValue.getValue()).doubleValue();
            double rightNum = ((Number) rightValue.getValue()).doubleValue();

            switch (op) {
                case "+": return new TypedValue(leftNum + rightNum, leftValue.getType());
                case "-": return new TypedValue(leftNum - rightNum, leftValue.getType());
                case "*": return new TypedValue(leftNum * rightNum, leftValue.getType());
                case "/":
                    if (rightNum == 0) throw new RuntimeException("Erro: Divisão por zero!");
                    return new TypedValue(leftNum / rightNum, leftValue.getType());
                case "==": return new TypedValue(leftNum == rightNum, "bool");
                case "!=": return new TypedValue(leftNum != rightNum, "bool");
                case "<": return new TypedValue(leftNum < rightNum, "bool");
                case "<=": return new TypedValue(leftNum <= rightNum, "bool");
                case ">": return new TypedValue(leftNum > rightNum, "bool");
                case ">=": return new TypedValue(leftNum >= rightNum, "bool");
                default: throw new RuntimeException("Operador inválido: " + op);
            }
        }

        // Comparações entre strings (==, !=, >, <, >=, <=)
        if (leftValue.getValue() instanceof String && rightValue.getValue() instanceof String) {
            String leftStr = (String) leftValue.getValue();
            String rightStr = (String) rightValue.getValue();

            switch (op) {
                case "==": return new TypedValue(leftStr.equals(rightStr), "bool");
                case "!=": return new TypedValue(!leftStr.equals(rightStr), "bool");
                case "<": return new TypedValue(leftStr.compareTo(rightStr) < 0, "bool");
                case "<=": return new TypedValue(leftStr.compareTo(rightStr) <= 0, "bool");
                case ">": return new TypedValue(leftStr.compareTo(rightStr) > 0, "bool");
                case ">=": return new TypedValue(leftStr.compareTo(rightStr) >= 0, "bool");
                default: throw new RuntimeException("Operador inválido para strings: " + op);
            }
        }

        // Comparações booleanas (==, !=)
        if (leftValue.getType().equals("bool") && rightValue.getType().equals("bool")) {
            boolean leftBool = (boolean) leftValue.getValue();
            boolean rightBool = (boolean) rightValue.getValue();

            switch (op) {
                case "==": return new TypedValue(leftBool == rightBool, "bool");
                case "!=": return new TypedValue(leftBool != rightBool, "bool");
                default: throw new RuntimeException("Operador inválido para booleanos: " + op);
            }
        }

        throw new RuntimeException("Operação inválida entre " + leftValue.getType() + " e " + rightValue.getType());
    }

    @Override
    public String toString() {
        return "BinaryExpression{" + left + " " + operator.getValue() + " " + right + "}";
    }
}
