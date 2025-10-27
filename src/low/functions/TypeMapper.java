package low.functions;

public class TypeMapper {
    public String toLLVM(String type) {
        if (type == null || type.isEmpty()) {
            throw new RuntimeException("Tipo inválido ou vazio");
        }

        type = type.trim();

        if (type.startsWith("List<") && type.endsWith(">")) {
            String inner = type.substring(5, type.length() - 1).trim();

            return switch (inner) {
                case "int"    -> "%struct.ArrayListInt*";
                case "double" -> "%struct.ArrayListDouble*";
                case "boolean"-> "%struct.ArrayListBool*";
                default       -> "i8*";
            };
        }


        if (type.startsWith("%") && type.endsWith("*")) {
            return type;
        }

        if (type.startsWith("Struct<") && type.endsWith(">")) {
            String inner = type.substring(7, type.length() - 1).trim();
            return "%" + inner.replace(".", "_") + "*";
        }

        if (type.startsWith("Struct ")) {
            String inner = type.substring("Struct ".length()).trim();
            return "%" + inner.replace(".", "_") + "*";
        }

        if (type.contains(".")) {
            String llvmName = "%" + type.replace(".", "_");
            return llvmName + "*";
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
            case "char" -> "i8";
            default -> {
                if (Character.isUpperCase(type.charAt(0))) {
                    yield "%" + type + "*";
                }
                throw new RuntimeException("Tipo não suportado: " + type);
            }
        };
    }
}
