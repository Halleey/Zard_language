package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;


import java.util.*;


public class DynamicMap {
    private final Map<ASTNode, ASTNode> entries;

    public DynamicMap() {
        this.entries = new LinkedHashMap<>();
    }

    public DynamicMap(Map<ASTNode, ASTNode> entries) {
        this.entries = entries;
    }

    public void put(ASTNode key, ASTNode value) {
        entries.put(key, value);
    }

    public TypedValue get(TypedValue key, RuntimeContext ctx) {
        for (Map.Entry<ASTNode, ASTNode> e : entries.entrySet()) {
            if (e.getKey().evaluate(ctx).equals(key)) {
                return e.getValue().evaluate(ctx);
            }
        }
        return new TypedValue("null", null);
    }

    public TypedValue remove(TypedValue key, RuntimeContext ctx) {
        Iterator<Map.Entry<ASTNode, ASTNode>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ASTNode, ASTNode> e = it.next();
            if (e.getKey().evaluate(ctx).equals(key)) {
                it.remove();
                return e.getValue().evaluate(ctx);
            }
        }
        return new TypedValue("null", null);
    }

    public int size() {
        return entries.size();
    }

    public Set<ASTNode> keyNodes() {
        return entries.keySet();
    }

    public Collection<ASTNode> valueNodes() {
        return entries.values();
    }

    @Override
    public String toString() {
        return entries.toString();
    }
}
