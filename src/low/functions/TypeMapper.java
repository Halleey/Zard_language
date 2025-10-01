package low.functions;

public class TypeMapper {

    public String toLLVM(String type) {
        switch (type) {
            case "i32", "double", "i1", "i8*", "void":
                return type;
        }
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string", "List", "var" -> "i8*";
            case "void" -> "void";
            default -> throw new RuntimeException("Tipo não suportado: " + type);
        };
    }
}
