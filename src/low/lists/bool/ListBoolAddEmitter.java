package low.lists.bool;

import ast.lists.ListAddNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
public class ListBoolAddEmitter {

    private final TempManager tempManager;

    public ListBoolAddEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListAddNode node, LLVMEmitVisitor visitor) {

        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);
        String listTemp = extractTemp(listCode);

        String valCode = node.getValuesNode().accept(visitor);
        llvm.append(valCode);
        String valTmp = extractTemp(valCode);

        llvm.append("  call void @arraylist_add_bool(%struct.ArrayListBool* ")
                .append(listTemp)
                .append(", i1 ").append(valTmp)
                .append(")\n");

        llvm.append(";;VAL:")
                .append(listTemp)
                .append(";;TYPE:%struct.ArrayListBool*\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        if (idx == -1)
            throw new RuntimeException("Não encontrou ;;VAL:");

        int end = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, end).trim();  // <-- agora trim!
    }
}
