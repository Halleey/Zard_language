package ast.expressions;

import ast.functions.FunctionNode;
import ast.runtime.RuntimeContext;

import java.util.Map;

public record
TypedValue(String type, Object value) {

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }

    public boolean isFunction() {
        return "function".equals(type);
    }

    public boolean isNamespace() {
        return "namespace".equals(type);
    }

    public Map<String, TypedValue> getNamespace() {
        if (!isNamespace()) {
            throw new RuntimeException("TypedValue is not a namespace");
        }
        RuntimeContext ctx = (RuntimeContext) value;
        return ctx.snapshotVariables();
    }


    public FunctionNode getFunction() {
        if (!isFunction()) throw new RuntimeException("TypedValue is not a function");
        return (FunctionNode) value;
    }
}
