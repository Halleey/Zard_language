package context.statics.list;

import ast.expressions.TypedValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ListValue {

    private final UUID id = UUID.randomUUID();
    private final String elementType;
    private final List<TypedValue> elements = new ArrayList<>();

    public ListValue(String elementType) {
        this.elementType = elementType;
    }

    public UUID getId() {
        return id;
    }

    public String getElementType() {
        return elementType;
    }

    public void add(TypedValue value) {
        if (!value.type().equals(elementType)) {
            throw new RuntimeException(
                    "Tipo inválido para lista <" + elementType + ">: " + value.type()
            );
        }
        elements.add(value);
    }

    public TypedValue get(int index) {
        if (index < 0 || index >= elements.size()) {
            throw new RuntimeException("Índice inválido: " + index);
        }
        return elements.get(index);
    }

    public TypedValue removeByIndex(int index) {
        if (index < 0 || index >= elements.size()) {
            throw new RuntimeException("Índice inválido: " + index);
        }
        return elements.remove(index);
    }

    public int size() {
        return elements.size();
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    public void clear() {
        elements.clear();
    }
}