package variables;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.Map;

public class UnaryOpNode extends ASTNode {
    public final String name;
    public final String operator; // "++" ou "--"

    public UnaryOpNode(String name, String operator) {
        this.name = name;
        this.operator = operator;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variável não declarada: " + name);
        }

        TypedValue current = variables.get(name);
        Object val = current.getValue();

        if (val instanceof Integer i) {
            i = operator.equals("++") ? i + 1 : i - 1;
            TypedValue newVal = new TypedValue("int", i);
            variables.put(name, newVal);
            return newVal;
        } else if (val instanceof Double d) {
            d = operator.equals("++") ? d + 1.0 : d - 1.0;
            TypedValue newVal = new TypedValue("double", d);
            variables.put(name, newVal);
            return newVal;
        } else {
            throw new RuntimeException("Incremento/decremento só é válido para int ou double");
        }
    }
}