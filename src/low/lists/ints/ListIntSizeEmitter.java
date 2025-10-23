package low.lists.ints;

import ast.lists.ListSizeNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;


public class ListIntSizeEmitter {
    private final TempManager tempManager;

    public ListIntSizeEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListSizeNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getNome().accept(visitor);

        llvm.append(listCode);

        String listPtr = extractLastVal(listCode);

        String sizeTmp = tempManager.newTemp();
        llvm.append("  ").append(sizeTmp)
                .append(" = call i32 @arraylist_size_int(%struct.ArrayListInt* ")
                .append(listPtr).append(")\n");

        llvm.append(";;VAL:").append(sizeTmp).append(";;TYPE:i32\n");

        return llvm.toString();
    }

    private String extractLastVal(String code) {
        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";
        int t = code.indexOf(";;TYPE:", v);
        return (t == -1) ? "" : code.substring(v + 6, t).trim();
    }
}
