package low.lists.doubles;

import ast.lists.ListAddNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListAddDoubleEmitter {
    private final TempManager tempManager;

    public ListAddDoubleEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListAddNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);
        String listTmp = extractTemp(listCode);

        String valCode = node.getValuesNode().accept(visitor);

        llvm.append(valCode);
        String valTemp = extractTemp(valCode);

        llvm.append("  call void @arraylist_add_double(%struct.ArrayListDouble* ").append(listTmp)
                .append(", double ").append(valTemp).append(")\n");

        llvm.append(";;VAL:").append(listTmp).append(";;TYPE:%struct.ArrayListDouble*\n");
        return llvm.toString();
    }


    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        if (idx == -1) throw new RuntimeException("Não encontrou ;;VAL:");
        int end = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, end);
    }
}
