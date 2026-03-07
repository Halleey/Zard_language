package context.statics.symbols;

public final class InputType implements Type {
    @Override
    public String name() {
        return "input";
    }

    @Override
    public boolean isNumeric() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof InputType;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
