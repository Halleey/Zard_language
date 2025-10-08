package low.lists.generics;

import ast.lists.ListSizeNode;
import low.TempManager;
import low.lists.ints.ListIntSizeEmitter;
import low.module.LLVMEmitVisitor;

public class ListSizeEmitter {
    private final TempManager tempManager;
    private final ListIntSizeEmitter intSizeEmitter;

    public ListSizeEmitter(TempManager tempManager) {
        this.intSizeEmitter = new ListIntSizeEmitter(tempManager);
        this.tempManager = tempManager;
    }

    public String emit(ListSizeNode node, LLVMEmitVisitor visitor) {
        String listCode = node.getNome().accept(visitor);
        String type = extractType(listCode);

        if (type.contains("ArrayListInt")) {
            return intSizeEmitter.emit(node, visitor);
        }

        StringBuilder llvm = new StringBuilder();
        llvm.append(listCode);

        String listValues = extractValue(listCode);
        String listCast = tempManager.newTemp();
        llvm.append("  ").append(listCast)
                .append(" = bitcast i8* ").append(listValues)
                .append(" to %ArrayList*\n");

        String sizeTmp = tempManager.newTemp();
        llvm.append("  ").append(sizeTmp)
                .append(" = call i32 @size(%ArrayList* ").append(listCast).append(")\n");
        llvm.append("  ;;VAL:").append(sizeTmp).append(";;TYPE:i32\n");

        return llvm.toString();
    }

    private String extractType(String code) {
        int typeIdx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", typeIdx);
        return code.substring(typeIdx + 7, endIdx == -1 ? code.length() : endIdx).trim();
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
