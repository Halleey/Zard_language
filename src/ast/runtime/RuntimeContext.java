package ast.runtime;

import ast.expressions.TypedValue;

import java.util.HashMap;
import java.util.Map;

public class RuntimeContext {
    private final Map<String, TypedValue> variables = new HashMap<>();
    private final RuntimeContext parent;


    public RuntimeContext() {
        this.parent = null;
    }

    public RuntimeContext(RuntimeContext parent) {
        this.parent = parent;
    }

    public void declareVariable(String name, TypedValue value) {
        if (variables.containsKey(name)) {
            throw new RuntimeException("Variável já declarada: " + name);
        }
        variables.put(name, value);
    }

    public void setVariable(String name, TypedValue value) {
        if (variables.containsKey(name)) {
            variables.put(name, value);
        } else if (parent != null) {
            parent.setVariable(name, value);
        } else {
            throw new RuntimeException("Variável não definida: " + name);
        }
    }

    public TypedValue getVariable(String name) {
        if (variables.containsKey(name)) return variables.get(name);
        if (parent != null) return parent.getVariable(name);
        throw new RuntimeException("Variável não definida: " + name);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name) || (parent != null && parent.hasVariable(name));
    }

    public Map<String, TypedValue> getVariables() {
        return variables;
    }
}