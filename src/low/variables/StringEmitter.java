package low.variables;

import low.TempManager;
import low.main.GlobalStringManager;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMString;

public class StringEmitter {

    private final TempManager temps;
    private final GlobalStringManager globals;
    private final VariableEmitter varEmitter;

    public StringEmitter(TempManager temps,
                         GlobalStringManager globals,
                         VariableEmitter varEmitter) {
        this.temps = temps;
        this.globals = globals;
        this.varEmitter = varEmitter;
    }

    public String emitStore(String name, LLVMValue value) {
        String ptr = varEmitter.getVarPtr(name);
        return "  store %String* " + value.getName() +
                ", %String** " + ptr + "\n";
    }

    public LLVMValue createEmptyString(String varName) {
        String emptyStrName = globals.getOrCreateString("");
        int len = globals.getLength("");

        String tmp = temps.newTemp();
        String ptr = varEmitter.getVarPtr(varName);

        StringBuilder sb = new StringBuilder();

        sb.append("  ").append(tmp)
                .append(" = call %String* @createString(i8* getelementptr ([")
                .append(len).append(" x i8], [")
                .append(len).append(" x i8]* ")
                .append(emptyStrName)
                .append(", i32 0, i32 0))\n");

        // faz store direto
        sb.append("  store %String* ")
                .append(tmp)
                .append(", %String** ")
                .append(ptr)
                .append("\n");

        return new LLVMValue(new LLVMString(), tmp, sb.toString());
    }
}