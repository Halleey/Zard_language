package low.inputs;
import ast.inputs.InputNode;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.*;

public class InputEmitter {

    private final TempManager tempManager;
    private final GlobalStringManager globalStringManager;

    public InputEmitter(TempManager tempManager, GlobalStringManager globalStringManager) {
        this.tempManager = tempManager;
        this.globalStringManager = globalStringManager;
    }

    public LLVMValue emit(InputNode node, LLVMTYPES type) {

        String argOperand;

        if (node.getPrompt() != null && !node.getPrompt().isEmpty()) {
            String globalName = globalStringManager.getOrCreateString(node.getPrompt());
            int byteLen = node.getPrompt().length() + 2;

            argOperand = "getelementptr ([" + byteLen + " x i8], [" + byteLen +
                    " x i8]* " + globalName + ", i32 0, i32 0)";
        } else {
            argOperand = "null";
        }

        StringBuilder code = new StringBuilder();

        if (type instanceof LLVMInt) {
            String tmp = tempManager.newTemp();

            code.append("  ").append(tmp)
                    .append(" = call i32 @inputInt(i8* ")
                    .append(argOperand).append(")\n");

            return new LLVMValue(new LLVMInt(), tmp, code.toString());
        }

        if (type instanceof LLVMDouble) {
            String tmp = tempManager.newTemp();

            code.append("  ").append(tmp)
                    .append(" = call double @inputDouble(i8* ")
                    .append(argOperand).append(")\n");

            return new LLVMValue(new LLVMDouble(), tmp, code.toString());
        }

        if (type instanceof LLVMBool) {
            String tmp = tempManager.newTemp();

            code.append("  ").append(tmp)
                    .append(" = call i1 @inputBool(i8* ")
                    .append(argOperand).append(")\n");

            return new LLVMValue(new LLVMBool(), tmp, code.toString());
        }

        if (type instanceof LLVMChar) {
            String tmp = tempManager.newTemp();

            code.append("  ").append(tmp)
                    .append(" = call i8 @inputChar(i8* ")
                    .append(argOperand).append(")\n");

            return new LLVMValue(new LLVMChar(), tmp, code.toString());
        }

        if (type instanceof LLVMString) {

            String rawStr = tempManager.newTemp();
            code.append("  ").append(rawStr)
                    .append(" = call i8* @inputString(i8* ")
                    .append(argOperand).append(")\n");

            String strObj = tempManager.newTemp();
            code.append("  ").append(strObj)
                    .append(" = call %String* @createString(i8* ")
                    .append(rawStr).append(")\n");

            return new LLVMValue(new LLVMString(), strObj, code.toString());
        }

        throw new RuntimeException("Tipo não suportado para input: " + type);
    }
}