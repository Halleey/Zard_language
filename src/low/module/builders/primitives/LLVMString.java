package low.module.builders.primitives;

import low.module.builders.LLVMTYPES;
public final class LLVMString implements LLVMTYPES {

    public LLVMPrimitiveKind kind() {
        return LLVMPrimitiveKind.STRING;
    }

    @Override
    public String toString() {
        return "%String*";
    }
}