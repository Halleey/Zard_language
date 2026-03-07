package context.statics.structs;

import context.statics.symbols.Type;


public final class StaticFields {

    private final String name;
    private final Type type;
    private final int index;
    private final int offset;


    public StaticFields(String name, Type type, int index, int offset) {
        this.name = name;
        this.type = type;
        this.index = index;
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return name + ": " + type +
                " (index=" + index +
                ", offset=" + offset + ")";
    }
}