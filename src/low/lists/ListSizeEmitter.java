package low.lists;

import ast.lists.ListSizeNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListSizeEmitter {
    private final TempManager tempManager;

    public ListSizeEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListSizeNode node, LLVMEmitVisitor visitor){
        StringBuilder llvm = new StringBuilder();
        String listCode = node.getNome().accept(visitor);
        llvm.append(listCode);
        String listValues = extractValue(listCode);
        String listCast = tempManager.newTemp();
        llvm.append("  ").append(listCast).append(" = bitcast i8* ").append(listValues).append(" to %ArrayList*\n");

        String sizeTmp = tempManager.newTemp();
        llvm.append("  ").append(sizeTmp)
                .append(" = call i32 @size(%ArrayList* ").append(listCast).append(")\n");
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
