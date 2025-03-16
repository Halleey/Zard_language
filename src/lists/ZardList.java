package lists;

import java.util.ArrayList;
import java.util.List;

public class ZardList {
    private final List<Object> elements;

    public ZardList() {
        this.elements = new ArrayList<>();
    }

    public void add(Object value) {
        elements.add(value);
    }

    public Object get(int index) {
        if (index < 0 || index >= elements.size()) {
            throw new RuntimeException("Erro: Índice fora dos limites da lista.");
        }
        return elements.get(index);
    }

    public void remove(int index) {
        if (index < 0 || index >= elements.size()) {
            throw new RuntimeException("Erro: Índice fora dos limites da lista.");
        }
        elements.remove(index);
    }

    public int size() {
        return elements.size();
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}

