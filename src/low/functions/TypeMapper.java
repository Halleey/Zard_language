package low.functions;
public class TypeMapper {
    public String toLLVM(String type) {
        if (type == null || type.isEmpty()) {
            throw new RuntimeException("Tipo inválido ou vazio");
        }

        type = type.trim();

        if (type.startsWith("List<") && type.endsWith(">")) {
            return "i8*";
        }

        if (type.startsWith("%") && type.endsWith("*")) {
            return type; // já é tipo LLVM válido
        }

        if (type.startsWith("Struct<") && type.endsWith(">")) {
            String inner = type.substring(7, type.length() - 1).trim();
            return "%" + inner + "*";
        }

        switch (type) {
            case "i32", "double", "i1", "i8*", "void", "%String*":
                return type;
        }

        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string" -> "%String*";
            case "void" -> "void";
            case "List" -> "i8*";
            default -> {
                if (Character.isUpperCase(type.charAt(0))) {
                    yield "%" + type + "*";
                }
                throw new RuntimeException("Tipo não suportado: " + type);
            }
        };
    }
}
