package context.statics.symbols;
import java.util.Objects;

public final class PrimitiveTypes implements Type {

    public static final PrimitiveTypes INT    = new PrimitiveTypes("int");
    public static final PrimitiveTypes DOUBLE = new PrimitiveTypes("double");
    public static final PrimitiveTypes FLOAT  = new PrimitiveTypes("float");
    public static final PrimitiveTypes BOOL   = new PrimitiveTypes("bool");
    public static final PrimitiveTypes STRING = new PrimitiveTypes("string");
    public static final PrimitiveTypes VOID   = new PrimitiveTypes("void");
    public static final PrimitiveTypes CHAR   = new PrimitiveTypes("char");
    public static final PrimitiveTypes ANY    = new PrimitiveTypes("any");
    private final String name;

    private PrimitiveTypes(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    public boolean isNumeric() {
        return this == INT || this == DOUBLE || this == FLOAT;
    }

    @Override
    public String toString() {
        return name;
    }
    @Override
    public boolean equals(Object o) {
        return this == o;
    }



    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}