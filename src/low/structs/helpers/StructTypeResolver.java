package low.structs.helpers;

import ast.structs.StructNode;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;
public class StructTypeResolver {

    private final LLVisitorMain visitorMain;
    private final TypeMapper typeMapper;

    public StructTypeResolver(LLVisitorMain visitorMain) {
        this.visitorMain = visitorMain;
        this.typeMapper = new TypeMapper();
    }

    public String resolveLLVMName(String logicalName) {
        StructNode n = visitorMain.getStructNode(logicalName);

        if (n != null && n.getLLVMName() != null && !n.getLLVMName().isBlank()) {
            return n.getLLVMName();
        }

        return logicalName;
    }

    public String toLLVMFieldType(Type type) {


        if (type instanceof ListType listType) {

            Type inner = listType.elementType();

            visitorMain.tiposDeListasUsados.add(inner);

            if (inner instanceof PrimitiveTypes prim) {

                return switch (prim.name()) {
                    case "int" -> "%struct.ArrayListInt*";
                    case "double" -> "%struct.ArrayListDouble*";
                    case "boolean", "bool" -> "%struct.ArrayListBool*";
                    case "string", "String" -> "%ArrayList*";
                    default -> "%ArrayList*";
                };
            }

            if (inner instanceof StructType structType) {
                return "%ArrayList*";
            }

            return "%ArrayList*";
        }


        if (type instanceof StructType structType) {

            String llvmName = resolveLLVMName(structType.name());

            return "%" + llvmName + "*";
        }



        if (type instanceof PrimitiveTypes prim) {
            return typeMapper.toLLVM(prim);
        }


        return "i8*";
    }
}