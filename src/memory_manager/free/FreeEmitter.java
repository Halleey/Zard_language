package memory_manager.free;

import low.TempManager;
import low.functions.TypeMapper;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

public class FreeEmitter {

    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper;
    private final TempManager temps;

    public FreeEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
        this.temps = temps;
        this.typeMapper = new TypeMapper();
        this.temps  = temps;
    }

    public String emit(FreeNode freeNode) {
        System.out.println("=== FreeEmitter: emit chamado ===");

        String varName = freeNode.getRoot().getSymbol().getName();
        System.out.println("Variável a ser liberada: " + varName);

        String varPtr = visitor.getVariableEmitter().getVarPtr(varName);
        System.out.println("Ponteiro LLVM da variável: " + varPtr);

        TypeInfos info = visitor.getVarType(varName);
        if (info == null) {
            System.out.println("Tipo da variável não encontrado, retornando vazio.");
            return "";
        }

        String llvmType = info.getLLVMType();
        String elemType = info.getElementType();
        System.out.println("LLVM type da variável: " + llvmType);
        System.out.println("Elemento da lista: " + elemType);
        System.out.println("É lista? " + info.isList());

        String tmpFree = temps.newTemp();
        System.out.println("Temp gerado para load: " + tmpFree);

        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(tmpFree)
                .append(" = load ").append(llvmType)
                .append(", ").append(llvmType).append("* ").append(varPtr).append("\n");

        if (info.isList()) {
            String freeFunc;

            if (elemType == null || elemType.equals("?") || llvmType.equals("%ArrayList*")) {
                // listas genéricas
                freeFunc = "@freeList";
                System.out.println("Lista genérica detectada. Função de free: " + freeFunc);
            } else {
                // listas tipadas
                freeFunc = typeMapper.freeFunctionForElement(elemType);
                System.out.println("Lista tipada. Função de free escolhida: " + freeFunc);
            }

            sb.append("  call void ").append(freeFunc)
                    .append("(").append(llvmType).append(" ").append(tmpFree).append(")\n");

            System.out.println("LLVM call gerado: " + sb.toString());
        }

        System.out.println("=== FreeEmitter: emit finalizado ===\n");

        return sb.toString();
    }
}
