package ast.maps;
import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.runtime.RuntimeContext;
import ast.variables.LiteralNode;

import java.util.*;
import java.util.*;

public class DynamicMap {
    private final Map<ASTNode, ASTNode> entries;
    private final String keyType;
    private final String valueType;

    public DynamicMap(String keyType, String valueType, Map<ASTNode, ASTNode> entries) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.entries = entries;
    }

    public String getKeyType() {
        return keyType;
    }

    public String getValueType() {
        return valueType;
    }

    public Map<ASTNode, ASTNode> getEntries() {
        return entries;
    }

    public Map<TypedValue, TypedValue> evaluate(RuntimeContext ctx) {
        Map<TypedValue, TypedValue> result = new LinkedHashMap<>();
        for (Map.Entry<ASTNode, ASTNode> e : entries.entrySet()) {
            TypedValue key = e.getKey().evaluate(ctx);
            TypedValue val = e.getValue().evaluate(ctx);
            result.put(key, val);
        }
        return result;
    }

    public int size() {
        return entries.size();
    }

    public void put(TypedValue key, TypedValue value) {
        if (!key.getType().equals(keyType)) {
            throw new RuntimeException("Invalid key type for Map<" + keyType + "," + valueType + ">: " + key.getType());
        }
        if (!value.getType().equals(valueType)) {
            throw new RuntimeException("Invalid value type for Map<" + keyType + "," + valueType + ">: " + value.getType());
        }
        entries.put(new LiteralNode(key), new LiteralNode(value));
    }

    @Override
    public String toString() {
        Map<TypedValue, TypedValue> vals = evaluate(new RuntimeContext());
        return vals.toString();
    }
}
