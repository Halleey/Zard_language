package low.lists.doubles;

import ast.lists.ListClearNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListDoubleClearEmitter {
    private final TempManager tempManager;
    public ListDoubleClearEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }


    public String emit(ListClearNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);

        String listTmp = extractTemp(listCode);

        llvm.append("  call void @arraylist_clear_double(%struct.ArrayListDouble* ").append(listTmp).append(")\n");
        llvm.append(";;VAL:").append(listTmp).append(";;TYPE:%struct.ArrayListDouble*\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        if (lastValIdx == -1 || typeIdx == -1) {
            throw new RuntimeException("Não foi possível extrair temp da lista no código LLVM:\n" + code);
        }
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
