package low.module.builders;


public final class LLVMPointer implements LLVMTYPES {

    private final LLVMTYPES pointee;

    public LLVMPointer(LLVMTYPES pointee) {
        this.pointee = pointee;
    }

    public LLVMTYPES pointee() {
        return pointee;
    }

    @Override
    public String toString() {
        return pointee.toString() + "*";
    }
}