package low.lists.bool;

import ast.lists.ListSizeNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListBoolSizeEmitter {

    private final TempManager tempManager;

    public ListBoolSizeEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }
    public String emit(ListSizeNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getNome().accept(visitor);
        llvm.append(listCode);

        String listPtr = extractValue(listCode);
        String sizeTmp = tempManager.newTemp();
        llvm.append(" ").append(sizeTmp).append(" = call i32 @arraylist_size_bool(%struct.ArrayListBool* ")
        .append(listPtr).append(")\n");
        //apenas para debug
        llvm.append("  ;;VAL:").append(sizeTmp).append(";;TYPE:i32\n");
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
