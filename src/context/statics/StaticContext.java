package context.statics;

import ast.functions.FunctionNode;
import context.statics.structs.StaticStructDefinition;

import java.util.*;


public final class StaticContext {

    private static int NEXT_ID = 0;

    private final int id;
    private final int depth;
    private final ScopeKind kind;

    private final StaticContext parent;
    private final List<StaticContext> children = new ArrayList<>();

    private final Map<String, Symbol> variables = new LinkedHashMap<>();
    private final Map<String, StaticStructDefinition> structs = new LinkedHashMap<>();
    private final Map<String, FunctionNode> functions = new HashMap<>();

    public void declareFunction(FunctionNode fn) {
        functions.put(fn.getName(), fn);
    }

    public FunctionNode resolveFunction(String name) {
        FunctionNode fn = functions.get(name);
        if (fn != null) return fn;
        if (parent != null) return parent.resolveFunction(name);
        throw new RuntimeException("Função não declarada: " + name);
    }



    private int nextSlot = 0;


    public StaticContext(ScopeKind kind) {
        this.id = NEXT_ID++;
        this.kind = kind;
        this.parent = null;
        this.depth = 0;
    }

    public StaticContext(ScopeKind kind, StaticContext parent) {
        this.id = NEXT_ID++;
        this.kind = kind;
        this.parent = parent;
        this.depth = parent.depth + 1;
        parent.children.add(this);
    }


    public int getId() {
        return id;
    }

    public int getDepth() {
        return depth;
    }

    public ScopeKind getKind() {
        return kind;
    }

    public StaticContext getParent() {
        return parent;
    }

    public List<StaticContext> getChildren() {
        return List.copyOf(children);
    }

    public Collection<Symbol> getDeclaredVariables() {
        return variables.values();
    }


    public void declareStruct(String name, StaticStructDefinition def) {
        if (structs.containsKey(name)) {
            throw new RuntimeException(
                    "Struct já declarado neste escopo: " + name
            );
        }
        structs.put(name, def);
    }

    public StaticStructDefinition resolveStruct(String name) {
        String base = normalizeStructName(name);

        StaticStructDefinition def = structs.get(base);
        if (def != null) return def;

        if (parent != null) return parent.resolveStruct(base);

        throw new RuntimeException("Struct não declarado: " + name);
    }

    private String normalizeStructName(String name) {
        int idx = name.indexOf('<');
        return idx == -1 ? name : name.substring(0, idx);
    }
    public Symbol declareVariable(String name, String type) {
        boolean isPrimitive = switch(type) {
            case "int", "double", "float", "bool", "char", "string" -> true;
            default -> false;  // structs, listas e outros tipos compostos
        };

        // percorre todos os pais até ROOT
        StaticContext cur = this.parent;
        while (cur != null) {
            // se algum pai já tem a variável, e o bloco atual é um loop, proíbe
            if (isPrimitive && cur.variables.containsKey(name) && this.kind.isLoop()) {
                throw new RuntimeException(
                        "Shadowing de variável primitiva não permitido dentro de loops ou blocos aninhados: '" + name + "'"
                );
            }
            cur = cur.parent;
        }

        // ok para shadowing em IF/ELSE ou para structs/listas
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


    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLoopScope() {
        return kind.isLoop();
    }

    public boolean isConditionalScope() {
        return kind.isConditional();
    }

    public boolean hasLifetimeBoundary() {
        return kind.hasLifetimeBoundary();
    }

    public boolean isAncestorOf(StaticContext other) {
        StaticContext cur = other;
        while (cur != null) {
            if (cur == this) return true;
            cur = cur.parent;
        }
        return false;
    }


    public void debugPrintRecursive(String indent) {
        System.out.println(
                indent + "Scope #" + id +
                        " depth=" + depth +
                        " [" + kind + "] {"
        );

        if (variables.isEmpty()) {
            System.out.println(indent + "  (nenhuma variável)");
        } else {
            for (Symbol sym : variables.values()) {
                System.out.println(indent + "  " + sym);
            }
        }

        for (StaticContext child : children) {
            child.debugPrintRecursive(indent + "  ");
        }

        System.out.println(indent + "}");
    }

}
