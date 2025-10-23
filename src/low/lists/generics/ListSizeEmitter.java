package low.lists.generics;

import ast.lists.ListSizeNode;
import low.TempManager;
import low.lists.bool.ListBoolSizeEmitter;
import low.lists.doubles.ListDoubleSizeEmitter;
import low.lists.ints.ListIntSizeEmitter;
import low.module.LLVMEmitVisitor;
public class ListSizeEmitter {
    private final TempManager tempManager;
    private final ListIntSizeEmitter intSizeEmitter;
    private final ListDoubleSizeEmitter doubleSizeEmitter;
    private final ListBoolSizeEmitter boolSizeEmitter;

    public ListSizeEmitter(TempManager tempManager) {
        this.intSizeEmitter = new ListIntSizeEmitter(tempManager);
        this.tempManager = tempManager;
        this.doubleSizeEmitter = new ListDoubleSizeEmitter(tempManager);
        this.boolSizeEmitter = new ListBoolSizeEmitter(tempManager);
    }

    public String emit(ListSizeNode node, LLVMEmitVisitor visitor) {
        String listCode = node.getNome().accept(visitor);

        String listType = extractType(listCode);

        if (listType.contains("ArrayListInt")) {
            return intSizeEmitter.emit(node, visitor);
        }

        if (listType.contains("ArrayListBool")) {
            return boolSizeEmitter.emit(node, visitor);
        }

        if (listType.contains("ArrayListDouble")) {
            return doubleSizeEmitter.emit(node, visitor);
        }

        StringBuilder llvm = new StringBuilder();
        llvm.append(listCode);

        String listPtr = extractLastVal(listCode);

        String listCast = tempManager.newTemp();
        llvm.append("  ").append(listCast)
                .append(" = bitcast i8* ").append(listPtr)
                .append(" to %ArrayList*\n");

        String sizeTmp = tempManager.newTemp();
        llvm.append("  ").append(sizeTmp)
                .append(" = call i32 @length(%ArrayList* ").append(listCast).append(")\n");

        llvm.append(";;VAL:").append(sizeTmp).append(";;TYPE:i32\n");

        return llvm.toString();
    }

    private String extractLastVal(String code) {
        String lastVal = "";
        for (String line : code.split("\n")) {
            if (line.contains(";;VAL:")) {
                String val = line.split(";;VAL:")[1].trim();
                if (val.contains(";;TYPE")) {
                    val = val.split(";;TYPE")[0].trim();
                }
                lastVal = val;
            }
        }
        return lastVal;
    }

    private String extractType(String code) {
        String lastType = "";
        for (String line : code.split("\n")) {
            if (line.contains(";;TYPE:")) {
                lastType = line.split(";;TYPE:")[1].trim();
            }
        }
        return lastType;
    }
}
