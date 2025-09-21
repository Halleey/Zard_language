package ast.maps;
import ast.expressions.TypedValue;
import java.util.*;

public class DynamicMap {
    private final Map<TypedValue, TypedValue> entries;

    public DynamicMap() {
        this.entries = new LinkedHashMap<>();
    }

    public void put(TypedValue key, TypedValue value) {
        entries.put(key, value);
    }

    public TypedValue get(TypedValue key) {
        return entries.getOrDefault(key, new TypedValue("null", null));
    }

    public TypedValue remove(TypedValue key) {
        TypedValue removed = entries.remove(key);
        return removed != null ? removed : new TypedValue("null", null);
    }

    public int size() {
        return entries.size();
    }

    public Set<TypedValue> keys() {
        return entries.keySet();
    }

    public Collection<TypedValue> values() {
        return entries.values();
    }

    @Override
    public String toString() {
        return entries.toString();
    }
}
