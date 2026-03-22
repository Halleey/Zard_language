package low.module.builders.primitives;

import low.module.builders.LLVMTYPES;

public final class LLVMGeneric implements LLVMTYPES {

    public LLVMPrimitiveKind kind() {
        return LLVMPrimitiveKind.GENERIC;
    }

    @Override
    public String toString() {
        return "i8";
    }
}