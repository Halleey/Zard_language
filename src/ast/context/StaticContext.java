package ast.context;

import ast.context.statics.Symbol;

import java.util.*;

public class StaticContext {

    private final StaticContext parent;
    private final List<StaticContext> children = new ArrayList<>();
    private final Map<String, Symbol> variables = new LinkedHashMap<>();
    private int nextSlot = 0;

    public StaticContext() {
        this.parent = null;
    }

    public StaticContext(StaticContext parent) {
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    public Symbol declareVariable(String name, String type) {
        if (variables.containsKey(name)) {
            throw new RuntimeException("Variável já declarada neste escopo: " + name);
        }

        Symbol sym = new Symbol(name, type, nextSlot++);
        variables.put(name, sym);
        return sym;
    }

    public Symbol resolveVariable(String name) {
        Symbol sym = variables.get(name);
        if (sym != null) return sym;
        if (parent != null) return parent.resolveVariable(name);
        throw new RuntimeException("Variável não declarada: " + name);
    }

    public void debugPrint(String indent) {
        System.out.println(indent + "Scope {");

        for (Symbol sym : variables.values()) {
            System.out.println(
                    indent + "  " + sym.getName()
                            + " : " + sym.getType()
                            + " (slot " + sym.getSlotIndex() + ")"
            );
        }

        for (StaticContext child : children) {
            child.debugPrint(indent + "  ");
        }

        System.out.println(indent + "}");
    }
}
