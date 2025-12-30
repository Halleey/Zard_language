package context.runtime;

import ast.expressions.TypedValue;
import ast.functions.FunctionNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableSlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeContext {
    private final Map<String, VariableSlot> variables = new HashMap<>();

    private final RuntimeContext parent;
    private final Map<String, StructDefinition> structTypes = new HashMap<>();
    private final Map<String, Map<String, FunctionNode>> structMethods = new HashMap<>();

    public Map<String, FunctionNode> getOrCreateStructMethodTable(String structName) {
        return structMethods.computeIfAbsent(structName, k -> new HashMap<>());
    }

    public FunctionNode getStructMethod(String structName, String methodName) {
        Map<String, FunctionNode> table = structMethods.get(structName);
        if (table != null && table.containsKey(methodName)) {
            return table.get(methodName);
        }

        if (parent != null) return parent.getStructMethod(structName, methodName);

        return null;
    }

    public void registerStructMethod(String structName, String methodName, FunctionNode fn) {
        structMethods
                .computeIfAbsent(structName, k -> new HashMap<>())
                .put(methodName, fn);
    }


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
        variables.put(name, new VariableSlot(value));
    }

    public void bindSlot(String name, VariableSlot slot) {
        variables.put(name, slot); // alias real
    }

    public VariableSlot getSlot(String name) {
        if (variables.containsKey(name)) return variables.get(name);
        if (parent != null) return parent.getSlot(name);
        throw new RuntimeException("Variável não definida: " + name);
    }

    public TypedValue getVariable(String name) {
        return getSlot(name).typedValue;
    }

    public void setVariable(String name, TypedValue value) {
        getSlot(name).typedValue = value;
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name) || (parent != null && parent.hasVariable(name));
    }

    public Map<String, TypedValue> snapshotVariables() {
        Map<String, TypedValue> out = new HashMap<>();
        for (var entry : variables.entrySet()) {
            out.put(entry.getKey(), entry.getValue().typedValue);
        }
        return out;
    }



    public void registerStructType(String name, List<VariableDeclarationNode> fields) {
        if (structTypes.containsKey(name)) {
            throw new RuntimeException("Struct já definida: " + name);
        }
        structTypes.put(name, new StructDefinition(name, fields));
    }

    public StructDefinition getStructType(String name) {
        if (name.contains("<")) {
            name = name.substring(0, name.indexOf('<'));
        }

        if (structTypes.containsKey(name)) return structTypes.get(name);
        if (parent != null) return parent.getStructType(name);

        throw new RuntimeException("Struct não definida: " + name);
    }

}