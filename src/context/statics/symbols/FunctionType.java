package context.statics.symbols;

import java.util.List;
import java.util.Objects;

public final class FunctionType implements Type {

    private final List<Type> parameterTypes;
    private final Type returnType;

    public FunctionType(List<Type> parameterTypes, Type returnType) {
        this.parameterTypes = List.copyOf(parameterTypes);
        this.returnType = returnType;
    }

    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return "(" +
                parameterTypes.stream()
                        .map(Object::toString)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("")
                + ") -> " + returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FunctionType other)) return false;
        return parameterTypes.equals(other.parameterTypes)
                && returnType.equals(other.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameterTypes, returnType);
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public boolean isNumeric() {
        return false;
    }
}