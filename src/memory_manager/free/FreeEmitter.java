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

        // Nome da variável agora vem do Symbol
        String varName = root.getSymbol().getName();

        String elementType = visitor.getListElementType(varName);
        if (elementType == null) {
            return "";
        }

        String llvmType = typeMapper.toLLVM("List<" + elementType + ">");
        String freeFunc = typeMapper.freeFunctionForElement(elementType);

        return String.format(
                "  %%tmpFree_%s = load %s, %s* %%%s\n" +
                        "  call void %s(%s %%tmpFree_%s)",
                varName, llvmType, llvmType, varName,
                freeFunc, llvmType, varName
        );
    }
}
