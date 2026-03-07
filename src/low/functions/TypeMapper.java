package low.functions;
import context.statics.symbols.*;
import low.utils.LLVMNameUtils;

public class TypeMapper {

    public String toLLVM(Type type) {
        if (type == null) throw new RuntimeException("Tipo inválido (null)");

        if (type instanceof PrimitiveTypes prim) {
            return switch (prim.name()) {
                case "int"     -> "i32";
                case "double"  -> "double";
                case "float"   -> "float";
                case "boolean" -> "i1";
                case "string"  -> "%String*";
                case "void"    -> "void";
                case "char"    -> "i8";
                default -> throw new RuntimeException("Tipo primitivo não suportado: " + prim.name());
            };
        }

        if (type instanceof ListType list) {
            Type elemType = list.elementType();
            boolean isRef = list.isReference();

            // Ex: List<int> -> %struct.ArrayListInt*
            String llvmInner;
            if (elemType instanceof PrimitiveTypes prim) {
                llvmInner = switch (prim.name()) {
                    case "int"     -> "%struct.ArrayListInt*";
                    case "double"  -> "%struct.ArrayListDouble*";
                    case "boolean" -> "%struct.ArrayListBool*";
                    case "string"  -> "%ArrayList*";
                    default       -> "%ArrayList*";
                };
            } else if (elemType instanceof StructType st) {
                String llvmName = LLVMNameUtils.llvmSafe(st.name());
                llvmInner = "%" + llvmName + "ArrayList*";
            } else {
                llvmInner = "%ArrayList*";
            }

            return isRef ? llvmInner + "*" : llvmInner;
        }

        if (type instanceof StructType st) {
            String llvmName = LLVMNameUtils.llvmSafe(st.name());
            return "%" + llvmName + "*";
        }

        if (type instanceof UnknownType) {
            return "i8*";
        }

        throw new RuntimeException("Tipo não suportado para LLVM: " + type);
    }


    public String freeFunctionForElement(Type elementType) {
        if (elementType instanceof PrimitiveTypes prim) {
            return switch (prim.name()) {
                case "int"     -> "@arraylist_free_int";
                case "double"  -> "@arraylist_free_double";
                case "boolean" -> "@arraylist_free_bool";
                case "string"  -> "@freeList";
                default       -> "@freeList";
            };
        }

        return "@freeList";
    }
}