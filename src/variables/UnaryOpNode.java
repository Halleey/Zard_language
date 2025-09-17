package variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
public class UnaryOpNode extends ASTNode {
    private final String name;
    private final String operator;

    public UnaryOpNode(String name, String operator) {
        this.name = name;
        this.operator = operator;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        if (!ctx.hasVariable(name)) {
            throw new RuntimeException("Variável não declarada: " + name);
        }

        TypedValue current = ctx.getVariable(name);
        Object val = current.getValue();

        // Incremento/Decremento em int
        if (val instanceof Integer i) {
            int result = operator.equals("++") ? i + 1 : i - 1;
            TypedValue newVal = new TypedValue("int", result);
            ctx.setVariable(name, newVal);
            return newVal;
        }

        // Incremento/Decremento em double
        if (val instanceof Double d) {
            double result = operator.equals("++") ? d + 1.0 : d - 1.0;
            TypedValue newVal = new TypedValue("double", result);
            ctx.setVariable(name, newVal);
            return newVal;
        }

        throw new RuntimeException("Incremento/decremento só é válido para int ou double");
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "UnaryOp: " + name + " " + operator);
    }
}
