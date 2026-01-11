package memory_manager.free;

import low.TempManager;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import memory_manager.ownership.graphs.OwnershipNode;

public class FreeEmitter {

    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper;
    private final TempManager temps;

    public FreeEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
        this.typeMapper = new TypeMapper();
        this.temps  = temps;
    }

    public String emit(FreeNode freeNode) {
        OwnershipNode root = freeNode.getRoot();
        StringBuilder llvm = new StringBuilder();

        // nome real da variável LLVM (%lista, %pessoas, etc)
        String varName = root.getSymbol().getName();

        // só listas têm free aqui
        String elementType = visitor.getListElementType(varName);
        if (elementType == null) {
            return "";
        }

        String llvmType = typeMapper.toLLVM("List<" + elementType + ">");
        String freeFunc = typeMapper.freeFunctionForElement(elementType);

        // temp para carregar a lista
        String tmpList = temps.newTempWithPrefix("free");

        llvm.append("  ")
                .append(tmpList)
                .append(" = load ")
                .append(llvmType)
                .append(", ")
                .append(llvmType)
                .append("* %")
                .append(varName)
                .append("\n");

        llvm.append("  call void ")
                .append(freeFunc)
                .append("(")
                .append(llvmType)
                .append(" ")
                .append(tmpList)
                .append(")\n");

        return llvm.toString();
    }
}
