package expressions;

public class TypedValue {
    private final Object value;
    private final String type;

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


    public boolean isTruthy() {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        return false; // Strings e outros tipos são considerados "false"
    }


    @Override
    public String toString() {
        return
                "value=" + value +
                ", type='" + type + '\'' +
                '}';
    }
}


