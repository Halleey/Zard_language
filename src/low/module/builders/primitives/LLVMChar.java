package low.module.builders.primitives;

import low.module.builders.LLVMTYPES;

public final class LLVMChar implements LLVMTYPES {
    public LLVMPrimitiveKind kind() {
        return LLVMPrimitiveKind.CHAR;
    }

    @Override
    public String toString() {
        return "i8";
    }
}
