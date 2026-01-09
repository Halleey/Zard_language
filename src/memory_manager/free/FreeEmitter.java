package memory_manager.free;

import context.statics.Symbol;
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
        Symbol sym = root.getSymbol();

        // ---------- identidade REAL da variável ----------
        String baseName = buildLLVMBaseName(sym);

        // elemento da lista precisa ser resolvido pelo Symbol
        String elementType = visitor.getListElementType(sym);
        if (elementType == null) {
            return "";
        }

        String llvmType = typeMapper.toLLVM("List<" + elementType + ">");
        String freeFunc = typeMapper.freeFunctionForElement(elementType);

        String tmpName = "tmpFree_" + baseName;

        StringBuilder sb = new StringBuilder();

        sb.append("  %")
                .append(tmpName)
                .append(" = load ")
                .append(llvmType)
                .append(", ")
                .append(llvmType)
                .append("* %")
                .append(baseName)
                .append("\n");

        sb.append("  call void ")
                .append(freeFunc)
                .append("(")
                .append(llvmType)
                .append(" %")
                .append(tmpName)
                .append(")\n");

        return sb.toString();
    }


    private String buildLLVMBaseName(Symbol sym) {
        StringBuilder sb = new StringBuilder();

        sb.append(sym.getName())
                .append("_s")
                .append(sym.getDeclaredIn().getId())
                .append("_slot")
                .append(sym.getSlotIndex());

        return sb.toString();
    }
}
