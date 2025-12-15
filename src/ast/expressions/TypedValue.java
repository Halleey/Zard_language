package ast.expressions;

import ast.functions.FunctionNode;
import ast.lists.DynamicList;
import ast.runtime.RuntimeContext;
import ast.variables.ListValue;
import ast.variables.StructValue;

import java.util.Map;
public class TypedValue {

    protected final String type;
    protected final Object value;

    public TypedValue(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String type() {
        return type;
    }

    public Object value() {
        return value;
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }

    // ===== Deep copy padrão =====
    public TypedValue deepCopy() {

        // Struct especializado
        if (this instanceof StructValue sv) {
            return sv.deepCopy();
        }

        // Lista dinâmica
        if (value instanceof ListValue list) {
            return new TypedValue(type, list.deepCopy());
        }

        // Primitivos, String, Function, Namespace
        return this;
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
        if (!isFunction()) {
            throw new RuntimeException("TypedValue is not a function");
        }
        return (FunctionNode) value;
    }
}

