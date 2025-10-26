package low.inputs;
import ast.inputs.InputNode;
import low.TempManager;
import low.main.GlobalStringManager;


public class InputEmitter {
    private final TempManager tempManager;
    private final GlobalStringManager globalStringManager;

    public InputEmitter(TempManager tempManager, GlobalStringManager globalStringManager) {
        this.tempManager = tempManager;
        this.globalStringManager = globalStringManager;
    }

    public String emit(InputNode node, String llvmType) {
        String argOperand;
        if (node.getPrompt() != null && !node.getPrompt().isEmpty()) {
            String globalName = globalStringManager.getOrCreateString(node.getPrompt());
            int byteLen = node.getPrompt().length() + 2;
            argOperand = "getelementptr ([" + byteLen + " x i8], [" + byteLen +
                    " x i8]* " + globalName + ", i32 0, i32 0)";
        } else {
            argOperand = "null";
        }

        String tmp = tempManager.newTemp();
        StringBuilder sb = new StringBuilder();

        switch (llvmType) {
            case "i32" -> {
                sb.append("  ").append(tmp).append(" = call i32 @inputInt(i8* ").append(argOperand).append(")\n");
                sb.append(";;VAL:").append(tmp).append(";;TYPE:i32\n");
            }
            case "double" -> {
                sb.append("  ").append(tmp).append(" = call double @inputDouble(i8* ").append(argOperand).append(")\n");
                sb.append(";;VAL:").append(tmp).append(";;TYPE:double\n");
            }
            case "i1" -> {
                sb.append("  ").append(tmp).append(" = call i1 @inputBool(i8* ").append(argOperand).append(")\n");
                sb.append(";;VAL:").append(tmp).append(";;TYPE:i1\n");
            }
            case "%String*" -> {
                String tmpStr = tempManager.newTemp();
                sb.append("  ").append(tmpStr)
                        .append(" = call i8* @inputString(i8* ").append(argOperand).append(")\n");

                String tmpStruct = tempManager.newTemp();
                sb.append("  ").append(tmpStruct)
                        .append(" = call %String* @createString(i8* ").append(tmpStr).append(")\n");

                sb.append(";;VAL:").append(tmpStruct).append(";;TYPE:%String*\n");
            }
            default -> throw new RuntimeException("Tipo desconhecido para input: " + llvmType);
        }

        return sb.toString();
    }
}
