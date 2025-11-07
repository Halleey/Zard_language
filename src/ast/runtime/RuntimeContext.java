package ast.runtime;

import ast.expressions.TypedValue;
import ast.functions.FunctionNode;
import ast.variables.VariableDeclarationNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeContext {
    private final Map<String, TypedValue> variables = new HashMap<>();
    private final RuntimeContext parent;
    private final Map<String, StructDefinition> structTypes = new HashMap<>();
    private final Map<String, Map<String, FunctionNode>> structMethods = new HashMap<>();

    public Map<String, FunctionNode> getOrCreateStructMethodTable(String structName) {
        return structMethods.computeIfAbsent(structName, k -> new HashMap<>());
    }

    public FunctionNode getStructMethod(String structName, String methodName) {
        Map<String, FunctionNode> table = structMethods.get(structName);
        if (table == null) return null;
        return table.get(methodName);
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
        variables.put(name, value);
    }


    public void registerStructType(String name, List<VariableDeclarationNode> fields) {
        if (structTypes.containsKey(name)) {
            throw new RuntimeException("Struct já definida: " + name);
        }
        structTypes.put(name, new StructDefinition(name, fields));
    }

    public StructDefinition getStructType(String name) {
        if (structTypes.containsKey(name)) return structTypes.get(name);
        if (parent != null) return parent.getStructType(name);
        throw new RuntimeException("Struct não definida: " + name);
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