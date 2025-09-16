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

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i).getValue());
            if (i < elements.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}