package low.structs.helpers;

import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.functions.TypeMapper;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;

import static context.statics.symbols.PrimitiveTypes.*;
public class StructFieldPrint {

    private final StructTypeResolver resolver;
    private final TypeMapper typeMapper = new TypeMapper();

    public StructFieldPrint(StructTypeResolver resolver) {
        this.resolver = resolver;
    }

    public LLVMValue emitPrintField(int index, Type fieldType, String llvmStructName, TempManager temps, StringBuilder llvmCode) {

        String ptrTemp = "%" + llvmStructName + "_ptr_" + index;
        String valTemp = "%" + llvmStructName + "_val_" + index;

        // GEP para acessar o campo da struct
        llvmCode.append("  ").append(ptrTemp)
                .append(" = getelementptr inbounds %")
                .append(llvmStructName)
                .append(", %")
                .append(llvmStructName)
                .append("* %p, i32 0, i32 ")
                .append(index)
                .append("\n");

        // ===== Campos primitivos =====
        if (fieldType instanceof PrimitiveTypes prim) {
            LLVMTYPES llvmTypeObj = TypeMapper.from(prim);
            String llvmType = llvmTypeObj.toString();

            llvmCode.append("  ").append(valTemp)
                    .append(" = load ")
                    .append(llvmType)
                    .append(", ")
                    .append(llvmType)
                    .append("* ")
                    .append(ptrTemp)
                    .append("\n");

            emitPrimitivePrint(llvmCode, prim, valTemp);

            return new LLVMValue(visitorToLLVMType(prim), valTemp, ""); // valor tipado do campo
        }

        // ===== Campos Struct =====
        else if (fieldType instanceof StructType structType) {
            String structName = structType.name();

            llvmCode.append("  ").append(valTemp)
                    .append(" = load %")
                    .append(structName)
                    .append("*, %")
                    .append(structName)
                    .append("** ")
                    .append(ptrTemp)
                    .append("\n");

            llvmCode.append("  call void @print_")
                    .append(structName)
                    .append("(%")
                    .append(structName)
                    .append("* ")
                    .append(valTemp)
                    .append(")\n");

            return new LLVMValue(new LLVMStruct(structName), valTemp, "");
        }

        // ===== Campos List =====
        else if (fieldType instanceof ListType listType) {
            String llvmListType = resolver.toLLVMFieldType(listType);

            llvmCode.append("  ").append(valTemp)
                    .append(" = load ")
                    .append(llvmListType)
                    .append(", ")
                    .append(llvmListType)
                    .append("* ")
                    .append(ptrTemp)
                    .append("\n");

            emitListPrint(llvmCode, listType, valTemp);

            return new LLVMValue(visitorToLLVMListType(listType), valTemp, "");
        }

        else {
            throw new RuntimeException("Unsupported struct field type: " + fieldType);
        }
    }

    // ===================== Helpers =====================

    private void emitPrimitivePrint(StringBuilder sb, PrimitiveTypes prim, String value) {
        if (prim.equals(PrimitiveTypes.INT)) {
            sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ")
                    .append(value)
                    .append(")\n");
        } else if (prim.equals(PrimitiveTypes.DOUBLE)) {
            sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ")
                    .append(value)
                    .append(")\n");
        } else if (prim.equals(PrimitiveTypes.BOOL)) {
            sb.append("  %vb = zext i1 ").append(value).append(" to i32\n");
            sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %vb)\n");
        } else if (prim.equals(PrimitiveTypes.STRING)) {
            sb.append("  call void @printString(%String* ").append(value).append(")\n");
        } else {
            throw new RuntimeException("Unsupported primitive print: " + prim);
        }
    }

    private void emitListPrint(StringBuilder sb, ListType listType, String value) {
        Type elemType = listType.elementType();
        String llvmListType = resolver.toLLVMFieldType(listType);

        if (elemType.equals(PrimitiveTypes.INT)) {
            sb.append("  call void @arraylist_print_int(")
                    .append(llvmListType).append(" ").append(value)
                    .append(")\n");
        } else if (elemType.equals(PrimitiveTypes.DOUBLE)) {
            sb.append("  call void @arraylist_print_double(")
                    .append(llvmListType).append(" ").append(value)
                    .append(")\n");
        } else if (elemType.equals(PrimitiveTypes.STRING)) {
            sb.append("  %tmp_list_cast = bitcast ")
                    .append(llvmListType).append(" ").append(value)
                    .append(" to %ArrayList*\n");
            sb.append("  call void @arraylist_print_string(%ArrayList* %tmp_list_cast)\n");
        } else {
            sb.append("  ; TODO print List<").append(elemType).append(">\n");
        }
    }

    // ===================== Conversão para LLVMType =====================
    private LLVMTYPES visitorToLLVMType(PrimitiveTypes prim) {
        switch (prim.name()) {
            case "int": return new LLVMInt();
            case "double": return new LLVMDouble();
            case "bool": return new LLVMBool();
            case "string": return new LLVMString();
            case "char": return new LLVMChar();
            default: return new LLVMGeneric();
        }
    }

    private LLVMTYPES visitorToLLVMListType(ListType listType) {
        Type elemType = listType.elementType();
        LLVMTYPES llvmElemType;
        if (elemType instanceof PrimitiveTypes prim) {
            llvmElemType = visitorToLLVMType(prim);
        } else if (elemType instanceof StructType structType) {
            llvmElemType = new LLVMStruct(structType.name());
        } else {
            llvmElemType = new LLVMGeneric();
        }
        return new LLVMArrayList(llvmElemType);
    }
}