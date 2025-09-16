package variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

public class UnaryOpNode extends ASTNode {
    public final String name;
    public final String operator; // "++" ou "--"

    public UnaryOpNode(String name, String operator) {
        this.name = name;
        this.operator = operator;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        if (ctx.hasVariable(name)) {
            throw new RuntimeException("Variável não declarada: " + name);
        }

        TypedValue current = ctx.getVariable(name);
        Object val = current.getValue();

        if (val instanceof Integer i) {
            i = operator.equals("++") ? i + 1 : i - 1;
            TypedValue newVal = new TypedValue("int", i);
            ctx.setVariable(name, newVal);
            return newVal;
        } else if (val instanceof Double d) {
            d = operator.equals("++") ? d + 1.0 : d - 1.0;
            TypedValue newVal = new TypedValue("double", d);
            ctx.setVariable(name, newVal);
            return newVal;
        } else {
            throw new RuntimeException("Incremento/decremento só é válido para int ou double");
        }
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "UnaryOp: " + name + " " + operator);
    }
}
