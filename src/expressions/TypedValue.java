package expressions;

public class TypedValue {
    private final String type;
    private final Object value;

    public TypedValue(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String getType() { return type; }
    public Object getValue() { return value; }

    @Override
    public String toString() {
        return value.toString();
    }
}
