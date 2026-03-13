package low.structs.helpers;

import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.functions.TypeMapper;

import static tokens.Token.TokenType.STRING;

public class StructFieldFree {

    private final StructTypeResolver resolver;
    private final TypeMapper typeMapper = new TypeMapper();

    public StructFieldFree(StructTypeResolver resolver) {
        this.resolver = resolver;
    }

    public void emitFree(StringBuilder sb, int index, Type fieldType, String llvmName) {

        String ptr = "%field_ptr_" + index;
        String val = "%field_val_" + index;

        sb.append("  ").append(ptr)
                .append(" = getelementptr inbounds %")
                .append(llvmName)
                .append(", %")
                .append(llvmName)
                .append("* %p, i32 0, i32 ")
                .append(index)
                .append("\n");
        if (fieldType == PrimitiveTypes.STRING) {
            sb.append("  ").append(val)
                    .append(" = load %String*, %String** ")
                    .append(ptr)
                    .append("\n");

            sb.append("  call void @freeString(%String* ")
                    .append(val)
                    .append(")\n");
        }

        else if (fieldType instanceof StructType structType) {

            String structName = structType.name();

            sb.append("  ").append(val)
                    .append(" = load %")
                    .append(structName)
                    .append("*, %")
                    .append(structName)
                    .append("** ")
                    .append(ptr)
                    .append("\n");

            sb.append("  call void @free_")
                    .append(structName)
                    .append("(%")
                    .append(structName)
                    .append("* ")
                    .append(val)
                    .append(")\n");
        }

        else if (fieldType instanceof ListType listType) {

            String llvmListType = resolver.toLLVMFieldType(listType);

            sb.append("  ").append(val)
                    .append(" = load ")
                    .append(llvmListType)
                    .append(", ")
                    .append(llvmListType)
                    .append("* ")
                    .append(ptr)
                    .append("\n");

            String freeFunc = typeMapper.freeFunctionForElement(listType.elementType());

            sb.append("  call void ")
                    .append(freeFunc)
                    .append("(")
                    .append(llvmListType)
                    .append(" ")
                    .append(val)
                    .append(")\n");
        }

    }
}