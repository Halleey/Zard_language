package low.lists.doubles;

import ast.lists.ListSizeNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
public class ListDoubleSizeEmitter {
    private final TempManager tempManager;

    public ListDoubleSizeEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListSizeNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getNome().accept(visitor);
        appendCodePrefix(llvm, listCode);
        String listPtr = extractLastVal(listCode);
        String listType = extractLastType(listCode);

        if (!listType.contains("%struct.ArrayListDouble*")) {
            llvm.append("; [WARN] Expected %struct.ArrayListDouble* but got: ").append(listType).append("\n");
        }

        String sizeTmp = tempManager.newTemp();
        llvm.append("  ").append(sizeTmp)
                .append(" = call i32 @arraylist_size_double(%struct.ArrayListDouble* ")
                .append(listPtr).append(")\n");

        llvm.append(";;VAL:").append(sizeTmp).append("\n");
        llvm.append(";;TYPE:i32\n");

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
