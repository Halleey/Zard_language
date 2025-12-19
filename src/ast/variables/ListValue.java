package ast.variables;


import ast.expressions.TypedValue;
import java.util.ArrayList;
import java.util.List;

public class ListValue {

    private final String innerType;
    private final List<TypedValue> elements;

    public ListValue(String innerType, List<TypedValue> elements) {
        this.innerType = innerType;
        this.elements = elements;
    }

    public String getInnerType() {
        return innerType;
    }

    public List<TypedValue> getElements() {
        return elements;
    }

    public void add(TypedValue v) {
        elements.add(v);
    }


    public TypedValue get(int index) {
        return elements.get(index);
    }

    public TypedValue removeByIndex(int index) {
        if (index < 0 || index >= elements.size()) {
            throw new RuntimeException("Índice fora do limite: " + index);
        }
        return elements.remove(index);
    }

    public int size() {
        return elements.size();
    }

    public ListValue deepCopy() {
        List<TypedValue> copy = new ArrayList<>();
        for (TypedValue v : elements) {
            copy.add(v.deepCopy());
        }
        return new ListValue(innerType, copy);
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
