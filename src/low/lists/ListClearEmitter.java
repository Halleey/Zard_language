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
        String listTmp = extractTemp(listCode);

        //  Cast de i8* para %ArrayList*
        String listCastTmp = temps.newTemp();
        llvm.append("  ").append(listCastTmp)
                .append(" = bitcast i8* ").append(listTmp)
                .append(" to %ArrayList*\n");

        // Chama a função de clear
        llvm.append("  call void @clearList(%ArrayList* ").append(listCastTmp).append(")\n");

        // Atualiza VAL/TYPE para fluxo do visitor
        llvm.append(";;VAL:").append(listCastTmp).append(";;TYPE:%ArrayList*\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
