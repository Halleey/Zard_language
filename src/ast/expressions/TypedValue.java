package ast.expressions;

import ast.functions.FunctionNode;
import ast.runtime.RuntimeContext;

import java.util.Map;

public class
TypedValue {
    private final String type;
    private final Object value;

    public TypedValue(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String getType() { return type; }

    public Object getValue() { return value; }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }

    // --- Helpers para namespaces e funções ---
    public boolean isFunction() {
        return "function".equals(type);
    }

    public boolean isNamespace() {
        return "namespace".equals(type);
    }

    public Map<String, TypedValue> getNamespace() {
        if (!isNamespace()) throw new RuntimeException("TypedValue is not a namespace");
        RuntimeContext ctx = (RuntimeContext) value;
        return ctx.getVariables();
    }

    public FunctionNode getFunction() {
        if (!isFunction()) throw new RuntimeException("TypedValue is not a function");
        return (FunctionNode) value;
    }
}
