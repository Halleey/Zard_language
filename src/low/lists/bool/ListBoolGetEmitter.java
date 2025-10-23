package low.lists.bool;

import ast.lists.ListGetNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListBoolGetEmitter {
    private final TempManager temps;

    public ListBoolGetEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListGetNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListName().accept(visitor);
        String listTemp = extractTemp(listCode);
        appendCodePrefix(llvm, listCode);

        String idxCode = node.getIndexNode().accept(visitor);
        String idxTemp = extractTemp(idxCode);
        appendCodePrefix(llvm, idxCode);

        String idx64 = temps.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ").append(idxTemp)
                .append(" to i64\n");

        String outPtr = temps.newTemp();
        llvm.append("  ").append(outPtr)
                .append(" = alloca i1\n");

        String success = temps.newTemp();
        llvm.append("  ").append(success)
                .append(" = call i1 @arraylist_get_bool(%struct.ArrayListBool* ")
                .append(listTemp).append(", i64 ").append(idx64)
                .append(", i1* ").append(outPtr).append(")\n");

        String value = temps.newTemp();
        llvm.append("  ").append(value)
                .append(" = load i1, i1* ").append(outPtr).append("\n");

        llvm.append(";;VAL:").append(value).append("\n");
        llvm.append(";;TYPE:i1\n");

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

    private String extractTemp(String code) {
        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";
        int t = code.indexOf(";;TYPE:", v);
        return (t == -1) ? "" : code.substring(v + 6, t).trim();
    }

}
