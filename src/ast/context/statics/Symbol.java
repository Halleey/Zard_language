package ast.context.statics;

import ast.context.StaticContext;

public class Symbol {
    private final String name;
    private final String type;
    private final int slotIndex;
    private final StaticContext declaredIn;

    public Symbol(String name, String type, int slotIndex, StaticContext declaredIn) {
        this.name = name;
        this.type = type;
        this.slotIndex = slotIndex;
        this.declaredIn = declaredIn;
    }

    public StaticContext getDeclaredIn() {
        return declaredIn;
    }

    public ScopeKind getScopeKind() {
        return declaredIn.getKind();
    }

    @Override
    public String toString() {
        return name + ":" + type +
                " (slot " + slotIndex + ")" +
                " declared in " + declaredIn.getKind() +
                " [scope #" + declaredIn.getId() + "]";
    }
}
