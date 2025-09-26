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

    // Recebe o tipo LLVM como argumento
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
        String inputFunc = switch (llvmType) {
            case "i32" -> "@inputInt";
            case "double" -> "@inputDouble";
            case "i1" -> "@inputBool";
            case "i8*" -> "@inputString";
            default -> throw new RuntimeException("Tipo desconhecido para input: " + llvmType);
        };

        // Chamar direto a função tipada
        return "  " + tmp + " = call " + llvmType + " " + inputFunc + "(i8* " + argOperand + ")\n"
                + ";;VAL:" + tmp + " ;;TYPE:" + llvmType + "\n";
    }

}