package memory_manager.free;

import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import memory_manager.ownership.graphs.OwnershipNode;

public class FreeEmitter {

    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper;
    public FreeEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
        this.typeMapper = new TypeMapper();
    }

    public String emit(FreeNode freeNode) {
        OwnershipNode root = freeNode.getRoot();

        // Nome da variável
        String varName = root.getId();
        System.out.println("[FreeEmitter][DEBUG] Var name: " + varName);

        // Tipo do elemento da lista
        String elementType = visitor.getListElementType(varName);
        System.out.println("[FreeEmitter][DEBUG] Raw varType from getListElementType: " + elementType);

        if (elementType == null) {
            System.out.println("[FreeEmitter][DEBUG] Tipo nulo, não é lista, ignorando free.");
            return "";
        }

        // LLVM type da lista (usando TypeMapper)
        String llvmType = typeMapper.toLLVM("List<" + elementType + ">");
        System.out.println("[FreeEmitter][DEBUG] LLVM Type mapeado: " + llvmType);

        // Função de free usando TypeMapper
        String freeFunc = typeMapper.freeFunctionForElement(elementType);
        System.out.println("[FreeEmitter][DEBUG] Função de free: " + freeFunc);

        // LLVM IR
        String ir = String.format(
                "  %%tmpFree_%s = load %s, %s* %%%s\n" +
                        "  call void %s(%s %%tmpFree_%s)",
                varName, llvmType, llvmType, varName,
                freeFunc, llvmType, varName
        );

        System.out.println("[FreeEmitter][DEBUG] IR gerado:\n" + ir);
        return ir;
    }

}