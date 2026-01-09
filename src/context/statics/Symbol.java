package context.statics;

public final class Symbol {

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


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public StaticContext getDeclaredIn() {
        return declaredIn;
    }

    public ScopeKind getScopeKind() {
        return declaredIn.getKind();
    }

    public int getDeclarationDepth() {
        return declaredIn.getDepth();
    }


    public boolean isGlobal() {
        return declaredIn.getKind() == ScopeKind.GLOBAL;
    }

    public boolean isLocalTo(StaticContext ctx) {
        return declaredIn.isAncestorOf(ctx);
    }

    public boolean diesAtScopeExit(StaticContext ctx) {
        return isLocalTo(ctx) && ctx.hasLifetimeBoundary();
    }




    @Override
    public String toString() {
        return name + ":" + type +
                " (slot " + slotIndex + ")" +
                " declared in " + declaredIn.getKind() +
                " [scope #" + declaredIn.getId() +
                ", depth=" + declaredIn.getDepth() + "]";
    }

    public Symbol rebased(String newName) {
        return new Symbol(
                newName,
                this.type,
                this.slotIndex,
                this.declaredIn
        );
    }

}
