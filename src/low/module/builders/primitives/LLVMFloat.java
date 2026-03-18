package low.module.builders.primitives;

import low.module.builders.LLVMTYPES;

public final class LLVMFloat implements LLVMTYPES {
    public LLVMPrimitiveKind kind() {
        return LLVMPrimitiveKind.FLOAT;
    }

    @Override
    public String toString() {
    return "float";
    }
}
