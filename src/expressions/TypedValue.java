package expressions;

public class TypedValue {
    private final Object value;
    private final String type; // "int", "double", "string", etc.

    public TypedValue(Object value, String type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}


