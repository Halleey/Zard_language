package low.structs.helpers;

import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.functions.TypeMapper;

import static context.statics.symbols.PrimitiveTypes.*;

public class StructFieldPrint {

    private final StructTypeResolver resolver;
    private final TypeMapper typeMapper = new TypeMapper();

    public StructFieldPrint(StructTypeResolver resolver) {
        this.resolver = resolver;
    }

    public void emitPrint(StringBuilder sb, int index, Type fieldType, String llvmName) {

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

        if (fieldType instanceof PrimitiveTypes prim) {

            String llvmType = typeMapper.toLLVM(prim);

            sb.append("  ").append(val)
                    .append(" = load ")
                    .append(llvmType)
                    .append(", ")
                    .append(llvmType)
                    .append("* ")
                    .append(ptr)
                    .append("\n");

            emitPrimitivePrint(sb, prim, val);
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

            sb.append("  call void @print_")
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

            emitListPrint(sb, listType, val);
        }
        else {
            throw new RuntimeException("Unsupported struct field type: " + fieldType);
        }
    }

    private void emitPrimitivePrint(StringBuilder sb, PrimitiveTypes prim, String value) {

        if (prim.equals(INT)) {
            sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ")
                    .append(value)
                    .append(")\n");

        } else if (prim.equals(DOUBLE)) {
            sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ")
                    .append(value)
                    .append(")\n");

        } else if (prim.equals(BOOL)) {
            sb.append("  %vb = zext i1 ").append(value).append(" to i32\n");
            sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %vb)\n");

        } else if (prim.equals(STRING)) {
            sb.append("  call void @printString(%String* ").append(value).append(")\n");

        } else {
            throw new RuntimeException("Unsupported primitive print: " + prim);
        }
    }
    private void emitListPrint(StringBuilder sb, ListType listType, String value) {

        Type elemType = listType.elementType();
        String llvmListType = resolver.toLLVMFieldType(listType);

        if (elemType.equals(INT)) {

            sb.append("  call void @arraylist_print_int(")
                    .append(llvmListType)
                    .append(" ")
                    .append(value)
                    .append(")\n");

        }
        else if (elemType.equals(DOUBLE)) {

            sb.append("  call void @arraylist_print_double(")
                    .append(llvmListType)
                    .append(" ")
                    .append(value)
                    .append(")\n");

        }
        else if (elemType.equals(STRING)) {

            sb.append("  %tmp_list_cast = bitcast ")
                    .append(llvmListType)
                    .append(" ")
                    .append(value)
                    .append(" to %ArrayList*\n");

            sb.append("  call void @arraylist_print_string(%ArrayList* %tmp_list_cast)\n");

        }
        else {
            sb.append("  ; TODO print List<")
                    .append(elemType)
                    .append(">\n");
        }
    }
}