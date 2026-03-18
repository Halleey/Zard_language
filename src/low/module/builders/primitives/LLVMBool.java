package low.module.builders.primitives;

import low.module.builders.LLVMTYPES;

public final class LLVMBool implements LLVMTYPES {
    public LLVMPrimitiveKind kind() {
        return LLVMPrimitiveKind.BOOL;
    }

    @Override
    public String toString() {
        return "i1";
    }
}
