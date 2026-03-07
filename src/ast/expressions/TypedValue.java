package ast.expressions;

import ast.functions.FunctionNode;
import context.runtime.RuntimeContext;

import java.util.Map;


import context.statics.symbols.Type;

import java.util.Map;

public record TypedValue(Type type, Object value) {

    public static final TypedValue VOID =
            new TypedValue(null, null);

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }

    public boolean isFunction() {
        return type != null && type.toString().equals("function");
    }

    public boolean isNamespace() {
        return type != null && type.toString().equals("namespace");
    }

    public Map<String, TypedValue> getNamespace() {
        if (!isNamespace()) {
            throw new RuntimeException("TypedValue is not a namespace");
        }
        RuntimeContext ctx = (RuntimeContext) value;
        return ctx.snapshotVariables();
    }

    public FunctionNode getFunction() {
        if (!isFunction())
            throw new RuntimeException("TypedValue is not a function");
        return (FunctionNode) value;
    }
}