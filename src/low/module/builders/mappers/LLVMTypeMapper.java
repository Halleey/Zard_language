package low.module.builders.mappers;

import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;

public class LLVMTypeMapper {

    public static LLVMTYPES from(Type type) {

        if (type == PrimitiveTypes.INT)
            return new LLVMInt();

        if (type == PrimitiveTypes.DOUBLE)
            return new LLVMDouble();

        if (type == PrimitiveTypes.FLOAT)
            return new LLVMFloat();

        if (type == PrimitiveTypes.BOOL)
            return new LLVMBool();

        if (type == PrimitiveTypes.CHAR)
            return new LLVMChar();

        if (type == PrimitiveTypes.STRING)
            return new LLVMString();

        if (type instanceof StructType st)
            return new LLVMPointer(new LLVMStruct(st.name()));

        if (type instanceof ListType lt) {
            LLVMTYPES element = from(lt.elementType());
            return new LLVMPointer(new LLVMArrayList(element));
        }

        throw new RuntimeException("Tipo não suportado: " + type);
    }
}