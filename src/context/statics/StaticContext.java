package context.statics;

import ast.functions.FunctionNode;
import ast.variables.TypeResolver;
import context.statics.structs.StaticStructDefinition;
import context.statics.symbols.*;
import helpers.debugs.Debug;

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

    public void declareFunction(FunctionNode fn) {
        functions.put(fn.getName(), fn);
    }

    public FunctionNode resolveFunction(String name) {
        FunctionNode fn = functions.get(name);
        if (fn != null) return fn;
        if (parent != null) return parent.resolveFunction(name);
        throw new RuntimeException("Função não declarada: " + name);
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

    public Symbol declareVariable(String name, Type type) {
        Debug.ENABLE = true;
        Debug.log("Tentando declarar variável '" + name + "' no escopo " + kind + " (id=" + id + ")");

        boolean isPrimitive = type instanceof PrimitiveTypes;

        StaticContext cur = this.parent;
        while (cur != null) {
            if (isPrimitive && cur.variables.containsKey(name) && this.kind.isLoop()) {
                throw new RuntimeException(
                        "Shadowing de variável primitiva não permitido dentro de loops: '" + name + "'"
                );
            }
            cur = cur.parent;
        }

        if (variables.containsKey(name)) {
            Debug.log("Variável já existe neste escopo: " + name);
            throw new RuntimeException(
                    "Variável já declarada neste escopo (" + kind + "): " + name
            );
        }

        Symbol sym = new Symbol(name, type, nextSlot++, this);
        variables.put(name, sym);

        Debug.log("Declarada com sucesso: '" + name + "' no escopo " + kind + " (id=" + id + ")");
        return sym;
    }

    public Symbol resolveVariable(String name) {
        Symbol sym = variables.get(name);
        if (sym != null) return sym;
        if (parent != null) return parent.resolveVariable(name);
        throw new RuntimeException("Variável não declarada: " + name);
    }

    public Type resolveType(Type type) {
        if (type instanceof StructType structType) {
            resolveStruct(structType.name()); // valida existência
        } else if (type instanceof ListType listType) {
            resolveType(listType.elementType()); // valida tipo interno
        }
        return type;
    }

    public int getId() { return id; }
    public int getDepth() { return depth; }
    public ScopeKind getKind() { return kind; }
    public StaticContext getParent() { return parent; }
    public List<StaticContext> getChildren() { return List.copyOf(children); }
    public Collection<Symbol> getDeclaredVariables() { return variables.values(); }

    public boolean isRoot() { return parent == null; }
    public boolean hasLifetimeBoundary() { return kind.hasLifetimeBoundary(); }

    public boolean isAncestorOf(StaticContext other) {
        StaticContext cur = other;
        while (cur != null) {
            if (cur == this) return true;
            cur = cur.parent;
        }
        return false;
    }

    public FunctionNode getStructMethod(String structName, String methodName) {

        StaticStructDefinition def = resolveStruct(structName);

        FunctionNode fn = def.getMethod(methodName);

        if (fn == null) {
            throw new RuntimeException(
                    "Método '" + methodName + "' não encontrado no struct '" + structName + "'"
            );
        }

        return fn;
    }

    public void registerStructMethod(String structName, FunctionNode fn) {

        StaticStructDefinition def = resolveStruct(structName);

        def.addMethod(fn);
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