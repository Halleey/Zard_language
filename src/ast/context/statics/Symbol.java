package ast.context.statics;

import ast.expressions.TypedValue;
public class Symbol {
    private final String name;
    private final String type;
    private final int slotIndex;

    public Symbol(String name, String type, int slotIndex) {
        this.name = name;
        this.type = type;
        this.slotIndex = slotIndex;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getSlotIndex() { return slotIndex; }

    @Override
    public String toString() {
        return name + ":" + type + " (slot " + slotIndex + ")";
    }
}
