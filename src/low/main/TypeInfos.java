package low.main;

import context.statics.symbols.ListType;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;

public class TypeInfos {

    private final Type type;          // tipo semântico real
    private final String llvmType;    // tipo físico LLVM

    public TypeInfos(Type type, String llvmType) {
        this.type = type;
        this.llvmType = llvmType;
    }

    public Type getType() {
        return type;
    }

    public String getLLVMType() {
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