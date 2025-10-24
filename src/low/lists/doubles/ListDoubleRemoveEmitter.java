package low.lists.doubles;

import ast.lists.ListRemoveNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
public class ListDoubleRemoveEmitter {
    private final TempManager tempManager;

    public ListDoubleRemoveEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListRemoveNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        appendCodePrefix(llvm, listCode);
        String listVal = extractLastVal(listCode);
        String listType = extractLastType(listCode);
        if (!listType.contains("%struct.ArrayListDouble*")) {
            llvm.append("; [WARN] Expected %struct.ArrayListDouble* but got: ").append(listType).append("\n");
        }

        String idxCode = node.getIndexNode().accept(visitor);
        appendCodePrefix(llvm, idxCode);
        String idxVal = extractLastVal(idxCode);
        String idxType = extractLastType(idxCode);

        String idx64 = tempManager.newTemp();
        switch (idxType) {
            case "i64" -> idx64 = idxVal;
            case "i32", "" -> llvm.append("  ").append(idx64).append(" = zext i32 ").append(idxVal).append(" to i64\n");
            case "i1" -> llvm.append("  ").append(idx64).append(" = zext i1 ").append(idxVal).append(" to i64\n");
            case "double" ->
                    llvm.append("  ").append(idx64).append(" = fptosi double ").append(idxVal).append(" to i64\n");
            default -> {
                llvm.append("; [WARN] Unexpected index type: ").append(idxType).append(" → assuming i32\n");
                llvm.append("  ").append(idx64).append(" = zext i32 ").append(idxVal).append(" to i64\n");
            }
        }

        llvm.append("  call void @arraylist_remove_double(%struct.ArrayListDouble* ")
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

    private String extractLastType(String code) {
        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "";
        int end = code.indexOf("\n", t);
        if (end == -1) end = code.length();
        return code.substring(t + 7, end).trim();
    }
}
