package low.lists.ints;

import ast.lists.ListAddNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
public class ListIntAddEmitter {
    private final TempManager temps;

    public ListIntAddEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListAddNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);
        String listTmp = extractTemp(listCode);

        String valCode = node.getValuesNode().accept(visitor);
        llvm.append(valCode);
        String valTmp = extractTemp(valCode);

        llvm.append("  call void @arraylist_add_int(%struct.ArrayListInt* ").append(listTmp)
                .append(", i32 ").append(valTmp).append(")\n");

        llvm.append(";;VAL:").append(listTmp).append(";;TYPE:%struct.ArrayListInt*\n");
        return llvm.toString();
    }

    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        if (idx == -1) throw new RuntimeException("Não encontrou ;;VAL:");
        int end = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, end);
    }
}
