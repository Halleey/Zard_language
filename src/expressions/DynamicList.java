package expressions;

import java.util.List;

public class DynamicList {
    private final List<TypedValue> elements;

    public DynamicList(List<TypedValue> elements) {
        this.elements = elements;
    }

    public void add(TypedValue value) {
        elements.add(value);
    }

    public TypedValue get(int index) {
        return elements.get(index);
    }

    public void set(int index, TypedValue value) {
        elements.set(index, value);
    }

    public int size() {
        return elements.size();
    }

    public List<TypedValue> getElements() {
        return elements;
    }
}