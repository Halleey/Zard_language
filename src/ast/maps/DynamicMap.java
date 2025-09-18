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

    // Armazena diretamente os nós da AST
    public void put(ASTNode key, ASTNode value) {
        entries.put(key, value);
    }

    // Avalia somente quando necessário
    public TypedValue get(TypedValue key, RuntimeContext ctx) {
        for (Map.Entry<ASTNode, ASTNode> e : entries.entrySet()) {
            TypedValue k = e.getKey().evaluate(ctx);
            if (k.getType().equals(key.getType()) && k.getValue().equals(key.getValue())) {
                return e.getValue().evaluate(ctx);
            }
        }
        return new TypedValue("null", null);
    }

    public TypedValue remove(TypedValue key, RuntimeContext ctx) {
        Iterator<Map.Entry<ASTNode, ASTNode>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ASTNode, ASTNode> e = it.next();
            TypedValue k = e.getKey().evaluate(ctx);
            if (k.getType().equals(key.getType()) && k.getValue().equals(key.getValue())) {
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
