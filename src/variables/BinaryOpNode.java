package variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

import java.util.Map;
public class BinaryOpNode extends ASTNode {
    public final ASTNode left;
    public final String operator;
    public final ASTNode right;

    public BinaryOpNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        Object l = unwrap(left.evaluate(ctx));
        Object r = unwrap(right.evaluate(ctx));

        // Operadores numéricos
        if (l instanceof Number ln && r instanceof Number rn) {
            double lv = ln.doubleValue();
            double rv = rn.doubleValue();
            return switch (operator) {
                case "+" -> new TypedValue("double", lv + rv);
                case "-" -> new TypedValue("double", lv - rv);
                case "*" -> new TypedValue("double", lv * rv);
                case "/" -> new TypedValue("double", lv / rv);
                case ">" -> new TypedValue("boolean", lv > rv);
                case "<" -> new TypedValue("boolean", lv < rv);
                case ">=" -> new TypedValue("boolean", lv >= rv);
                case "<=" -> new TypedValue("boolean", lv <= rv);
                case "==" -> new TypedValue("boolean", lv == rv);
                case "!=" -> new TypedValue("boolean", lv != rv);
                default -> throw new RuntimeException("Operador inválido: " + operator);
            };
        }

        // Comparações ou concatenação de strings
        return switch (operator) {
            case "+" -> new TypedValue("string", l.toString() + r.toString());
            case "==" -> new TypedValue("boolean", l.equals(r));
            case "!=" -> new TypedValue("boolean", !l.equals(r));
            default -> throw new RuntimeException("Tipos incompatíveis para operador: " + operator);
        };
    }

    private Object unwrap(TypedValue tv) {
        return tv.getValue();
    }
}
