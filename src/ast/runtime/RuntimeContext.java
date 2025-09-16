package ast.runtime;

import expressions.TypedValue;

import java.util.HashMap;
import java.util.Map;

public class RuntimeContext {
    private final Map<String, TypedValue> variables = new HashMap<>();


    public void declareVariable(String name, TypedValue value) {
        if (variables.containsKey(name)) {
            throw new RuntimeException("Variável já declarada: " + name);
        }
        variables.put(name, value);
    }

    public void setVariable(String name, TypedValue value) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variável não definida: " + name);
        }
        variables.put(name, value);
    }

    public TypedValue getVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variável não definida: " + name);
        }
        return variables.get(name);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

}
