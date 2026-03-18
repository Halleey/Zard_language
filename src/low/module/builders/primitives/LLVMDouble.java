package low.module.builders.primitives;

import low.module.builders.LLVMTYPES;

public final class LLVMDouble implements LLVMTYPES {
    public LLVMPrimitiveKind kind() {
        return LLVMPrimitiveKind.DOUBLE;
    }

    @Override
    public String toString() {
        return "double";
    }
}
