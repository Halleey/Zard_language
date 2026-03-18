package low.module.builders.primitives;

import low.module.builders.LLVMTYPES;

public final class LLVMInt implements LLVMTYPES {
    public LLVMPrimitiveKind kind() {
        return LLVMPrimitiveKind.INT;
    }

    @Override
    public String toString() {
        return "i32";
    }

}
