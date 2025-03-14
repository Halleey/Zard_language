package variables;

import expressions.TypedValue;

import java.util.HashMap;
import java.util.Map;

public class VariableTable {
    private final Map<String, TypedValue> variables = new HashMap<>();

    public void setVariable(String name, TypedValue value) {
        variables.put(name, value);
    }

    public TypedValue getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public String toString() {
        return variables.toString();
    }


    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
}
