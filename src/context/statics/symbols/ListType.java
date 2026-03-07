package context.statics.symbols;

public final class ListType implements Type {

    private final Type elementType;
    private final boolean isReference;

    public ListType(Type elementType, boolean isReference) {
        this.elementType = elementType;
        this.isReference = isReference;
    }

    public static ListType ref(Type elementType) {
        return new ListType(elementType, true);
    }

    public static ListType value(Type elementType) {
        return new ListType(elementType, false);
    }

    public Type elementType() {
        return elementType;
    }

    public boolean isReference() {
        return isReference;
    }

    @Override
    public String name() {
        return "List";
    }

    @Override
    public boolean isNumeric() {
        return false;
    }

    @Override
    public String toString() {
        return "List<" + elementType +
                (isReference ? "*" : "") + ">";
    }
}