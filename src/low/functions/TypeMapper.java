package low.functions;
import context.statics.symbols.*;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;
import low.utils.LLVMNameUtils;

public class TypeMapper {


        public static LLVMTYPES from(Type type) {

            if (type instanceof PrimitiveTypes prim) {
                return switch (prim.name()) {
                    case "int"     -> new LLVMInt();
                    case "double"  -> new LLVMDouble();
                    case "float"   -> new LLVMFloat();
                    case "boolean" -> new LLVMBool();
                    case "string"  -> new LLVMString();
                    case "char"    -> new LLVMChar();
                    case "void"    -> new LLVMVoid();
                    default -> throw new RuntimeException("Tipo primitivo não suportado: " + prim.name());
                };
            }

            if (type instanceof ListType list) {
                LLVMTYPES elem = from(list.elementType());
                return new LLVMArrayList(elem);
            }

            if (type instanceof StructType st) {
                return new LLVMStruct(st.name());
            }

            if (type instanceof UnknownType) {
                return new LLVMGeneric(); // fallback genérico (i8*)
            }

            throw new RuntimeException("Tipo não suportado: " + type);
        }


    public String freeFunctionForElement(Type elementType) {
        if (elementType instanceof PrimitiveTypes prim) {
            return switch (prim.name()) {
                case "int"     -> "@arraylist_free_int";
                case "double"  -> "@arraylist_free_double";
                case "bool" -> "@arraylist_free_bool";
                case "string"  -> "@freeList";
                default       -> "@freeList";
            };
        }

        return "@freeList";
    }
}