package context.statics.symbols;


import java.util.Objects;

public final class UnknownType implements Type {

    public static final UnknownType UNKNOWN_TYPE = new UnknownType();

    private UnknownType() {
    }

    @Override
    public String name() {
        return "?";
    }

    @Override
    public boolean isNumeric() {
        return false;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UnknownType;
    }

    @Override
    public int hashCode() {
        return Objects.hash("unknown");
    }
}