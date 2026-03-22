package low.module.builders.structs;

import low.module.builders.LLVMTYPES;

public final class LLVMStruct implements LLVMTYPES {

    private final String name;

    public String getName() {
        return name;
    }

    public LLVMStruct(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "%" + name;
    }
}