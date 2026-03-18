package low.module.builders.lists;
import low.module.builders.LLVMTYPES;
import low.module.builders.primitives.*;


public final class LLVMArrayList implements LLVMTYPES {

    private final LLVMTYPES elementType;

    public LLVMArrayList(LLVMTYPES elementType) {
        this.elementType = elementType;
    }

    public LLVMTYPES elementType() {
        return elementType;
    }

    @Override
    public String toString() {

        if (elementType instanceof LLVMInt)
            return "%struct.ArrayListInt";

        if (elementType instanceof LLVMDouble)
            return "%struct.ArrayListDouble";

        if (elementType instanceof LLVMFloat)
            return "%struct.ArrayListFloat";

        if (elementType instanceof LLVMBool)
            return "%struct.ArrayListBool";

        if (elementType instanceof LLVMChar)
            return "%struct.ArrayListChar";

        if (elementType instanceof LLVMString)
            return "%struct.ArrayListString";

        return "%ArrayList";
    }
}