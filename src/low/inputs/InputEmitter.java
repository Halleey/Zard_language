package low.inputs;
import ast.inputs.InputNode;
import low.TempManager;
import low.main.GlobalStringManager;

public class InputEmitter {

    private final TempManager tempManager;
    private final GlobalStringManager globalStringManager;

    public InputEmitter(TempManager tempManager,
                        GlobalStringManager globalStringManager) {
        this.tempManager = tempManager;
        this.globalStringManager = globalStringManager;
    }

    public String emit(InputNode node) {
        String prompt = node.getPrompt();
        String argOperand;

        if (prompt != null && !prompt.isEmpty()) {
            // Garante que a constante global da string seja criada
            String globalName = globalStringManager.getOrCreateString(prompt);

            // Calcula o tamanho da string (já tratado dentro do GlobalStringManager)
            int byteLen = prompt.length() + 2; // +2: \0 e \n

            // Gera o getelementptr para passar como argumento
            argOperand = "getelementptr ([" + byteLen + " x i8], [" + byteLen +
                    " x i8]* " + globalName + ", i32 0, i32 0)";
        } else {
            // Sem prompt, passa null
            argOperand = "null";
        }

        // Novo temporário para receber o DynValue retornado
        String tmp = tempManager.newTemp();

        // Chamada para a função input com o prompt tratado
        return "  " + tmp + " = call %DynValue* @input(i8* " + argOperand + ")\n" +
                ";;VAL:" + tmp + " ;;TYPE:dyn";
    }
}
