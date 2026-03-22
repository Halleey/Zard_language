package low.main;

import context.statics.symbols.ListType;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.module.builders.LLVMTYPES;


public class TypeInfos {

    private final Type type;              // tipo semântico
    private final LLVMTYPES llvmType;     // tipo LLVM real (fortemente tipado)

    public TypeInfos(Type type, LLVMTYPES llvmType) {
        this.type = type;
        this.llvmType = llvmType;
    }

    public Type getType() {
        return type;
    }

    public LLVMTYPES getLLVMType() {
        return llvmType;
    }

    public boolean isList() {
        return type instanceof ListType;
    }

    public boolean isStruct() {
        return type instanceof StructType;
    }

    @Override
    public String toString() {
        return "TypeInfo{type=" + type + ", llvm=" + llvmType + "}";
    }
}