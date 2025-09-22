package low.lists;

import ast.lists.ListClearNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListClearEmitter {
    private final TempManager temps;

    public ListClearEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListClearNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);
        String listVal = extractValue(listCode);

        // Cast de i8* para %ArrayList*
        String listCast = temps.newTemp();
        llvm.append("  ").append(listCast)
                .append(" = bitcast i8* ").append(listVal)
                .append(" to %ArrayList*\n");

        llvm.append("  call void @clearList(%ArrayList* ").append(listCast).append(")\n");

        return llvm.toString();
    }

    private String extractValue(String code) {
        for (String line : code.split("\n")) {
            if (line.contains(";;VAL:")) {
                String val = line.split(";;VAL:")[1].trim();
                if (val.contains(";;TYPE")) val = val.split(";;TYPE")[0].trim();
                return val;
            }
        }
        return null;
    }
}
