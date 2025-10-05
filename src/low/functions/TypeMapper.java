package low.functions;public class TypeMapper {


    public String toLLVM(String type) {
        if (type == null || type.isEmpty()) {
            throw new RuntimeException("Tipo inválido ou vazio");
        }
        if (type.startsWith("List<") && type.endsWith(">")) {
            return "i8*";
        }

        switch (type) {
            case "i32", "double", "i1", "i8*", "void", "%String*":
                return type;
        }

        // Conversão de tipos da linguagem AST para LLVM
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string" -> "%String*";
            case "void" -> "void";
            case "List" -> "i8*";
            default -> throw new RuntimeException("Tipo não suportado: " + type);
        };
    }
}
