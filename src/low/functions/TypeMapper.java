package low.functions;
import low.utils.LLVMNameUtils;

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
                case "string" -> "%ArrayList*";
                default       -> "%ArrayList*";
            };
        }

        if (type.startsWith("%") && type.endsWith("*")) {
            return type;
        }

        if (type.startsWith("Struct<") && type.endsWith(">")) {
            String inner = type.substring(7, type.length() - 1).trim();

            if (inner.contains("<")) {
                String base = inner.substring(0, inner.indexOf('<'));
                String elem = inner.substring(inner.indexOf('<') + 1, inner.indexOf('>'));
                String llvmName = LLVMNameUtils.llvmSafe(base + "_" + elem);
                return "%" + llvmName + "*";
            } else {
                String llvmName = LLVMNameUtils.llvmSafe(inner);
                return "%" + llvmName + "*";
            }
        }


        if (type.startsWith("Struct ")) {
            String inner = type.substring("Struct ".length()).trim();
            String llvmName = LLVMNameUtils.llvmSafe(inner);
            return "%" + llvmName + "*";
        }

        if (type.contains(".")) {
            String llvmName = "%" + LLVMNameUtils.llvmSafe(type);
            return llvmName + "*";
        }

        switch (type) {
            case "i32", "double", "i1", "i8*", "void", "%String*": return type;
        }

        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "float" -> "float";
            case "boolean" -> "i1";
            case "string" -> "%String*";
            case "void" -> "void";
            case "List" -> "i8*";
            case "char" -> "i8";
            default -> {
                if (Character.isUpperCase(type.charAt(0))) {
                    yield "%" + LLVMNameUtils.llvmSafe(type) + "*";
                }
                throw new RuntimeException("Tipo não suportado: " + type);
            }
        };
    }


    public String freeFunctionForElement(String elementType) {
        return switch (elementType) {
            case "int" -> "@arraylist_free_int";
            case "double" -> "@arraylist_free_double";
            case "boolean" -> "@arraylist_free_bool";
            case "String" -> "@arraylist_free_ptr";
            default -> "@arraylist_free_ptr";
        };
    }



}
