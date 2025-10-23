package low.lists.bool;

import ast.lists.ListRemoveNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;


public class ListBoolRemoveEmitter {
    private final TempManager tempManager;

    public ListBoolRemoveEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListRemoveNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        appendCodePrefix(llvm, listCode);
        String listVal = extractLastVal(listCode); // ÚLTIMO ;;VAL:

        String idxCode = node.getIndexNode().accept(visitor);
        appendCodePrefix(llvm, idxCode);
        String idxVal = extractLastVal(idxCode);

        String idx64 = tempManager.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ").append(idxVal).append(" to i64\n");

        llvm.append("  call void @arraylist_remove_bool(%struct.ArrayListBool* ")
                .append(listVal).append(", i64 ").append(idx64).append(")\n");

        return llvm.toString();
    }

    private void appendCodePrefix(StringBuilder llvm, String code) {
        int marker = code.lastIndexOf(";;VAL:");
        String prefix = (marker == -1) ? code : code.substring(0, marker);
        if (!prefix.isEmpty()) {
            if (!prefix.endsWith("\n")) prefix += "\n";
            llvm.append(prefix);
        }
    }

    private String extractLastVal(String code) {
        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";
        int t = code.indexOf(";;TYPE:", v);
        return (t == -1) ? "" : code.substring(v + 6, t).trim();
    }
}
