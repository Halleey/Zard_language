package low.lists.ints;

import ast.lists.ListRemoveNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListRemoveIntEmitter {
    private final TempManager temps;

    public ListRemoveIntEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListRemoveNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        appendCodePrefix(llvm, listCode);
        String listVal = extractValue(listCode);

        String idxCode = node.getIndexNode().accept(visitor);
        appendCodePrefix(llvm, idxCode);
        String idxVal = extractValue(idxCode);

        // Cast do índice para i64
        String idx64 = temps.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ").append(idxVal).append(" to i64\n");

        llvm.append("  call void @arraylist_remove_int(%struct.ArrayListInt* ")
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

    private String extractValue(String code) {
        for (String line : code.split("\n")) {
            if (line.contains(";;VAL:")) {
                String val = line.split(";;VAL:")[1].trim();
                if (val.contains(";;TYPE")) {
                    val = val.split(";;TYPE")[0].trim();
                }
                return val;
            }
        }
        return null;
    }
}
