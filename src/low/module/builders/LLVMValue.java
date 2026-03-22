package low.module.builders;

public class LLVMValue {
    private final LLVMTYPES type;
    private final String name;  // SSA codegen
    private final String code;
    public LLVMValue(LLVMTYPES type, String name, String code) {
        this.type = type;
        this.name = name;
        this.code = code;
    }

    public LLVMTYPES getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
