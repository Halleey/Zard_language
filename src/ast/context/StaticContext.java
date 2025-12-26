package ast.context;

import ast.context.statics.ScopeKind;
import ast.context.statics.Symbol;

import java.util.*;
import java.util.*;

public class StaticContext {

    private static int NEXT_ID = 0;

    private final int id;
    private final ScopeKind kind;
    private final StaticContext parent;
    private final List<StaticContext> children = new ArrayList<>();

    private final Map<String, Symbol> variables = new LinkedHashMap<>();
    private final Map<String, StructDefinition> structs = new LinkedHashMap<>();

    private int nextSlot = 0;


    public StaticContext(ScopeKind kind) {
        this.id = NEXT_ID++;
        this.kind = kind;
        this.parent = null;
    }

    public StaticContext(ScopeKind kind, StaticContext parent) {
        this.id = NEXT_ID++;
        this.kind = kind;
        this.parent = parent;
        parent.children.add(this);
    }

    public int getId() {
        return id;
    }

    public ScopeKind getKind() {
        return kind;
    }

    public StaticContext getParent() {
        return parent;
    }

    public List<StaticContext> getChildren() {
        return children;
    }

    public Collection<Symbol> getVariables() {
        return variables.values();
    }


    public void declareStruct(String name, StructDefinition def) {
        if (structs.containsKey(name)) {
            throw new RuntimeException(
                    "Struct já declarado neste escopo: " + name
            );
        }
        structs.put(name, def);
    }

    public StructDefinition resolveStruct(String name) {
        StructDefinition def = structs.get(name);
        if (def != null) return def;

        if (parent != null) return parent.resolveStruct(name);

        throw new RuntimeException("Struct não declarado: " + name);
    }


    public Symbol declareVariable(String name, String type) {
        if (variables.containsKey(name)) {
            throw new RuntimeException(
                    "Variável já declarada neste escopo (" + kind + "): " + name
            );
        }

        Symbol sym = new Symbol(name, type, nextSlot++, this);
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
        System.out.println(
                indent + "Scope #" + id + " [" + kind + "] {"
        );

        for (Symbol sym : variables.values()) {
            System.out.println(
                    indent + "  " + sym
            );
        }

        for (StaticContext child : children) {
            child.debugPrint(indent + "  ");
        }

        System.out.println(indent + "}");
    }
}
