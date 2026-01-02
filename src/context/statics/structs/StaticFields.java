package context.statics.structs;

public class StaticFields {
    private final String name;
    private final String type;
    private final int index;
    private final int offset;

    public StaticFields(String name, String type, int index, int offset) {
        this.name = name;
        this.type = type;
        this.index = index;
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public int getOffset() {
        return offset;
    }
}
