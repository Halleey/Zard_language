package low.lists;

import ast.lists.ListGetNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
public class ListGetEmitter {
    private final TempManager tempManager;

    public ListGetEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListGetNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();


        String listLLVM = node.getListName().accept(visitor);
        String listTemp = extractTemp(listLLVM);


        int marker = listLLVM.lastIndexOf(";;VAL:");
        String codePrefix = (marker == -1) ? listLLVM : listLLVM.substring(0, marker);
        if (!codePrefix.isEmpty()) {
            if (!codePrefix.endsWith("\n")) codePrefix += "\n";
            llvm.append(codePrefix);
        }

        String arrayPtr = tempManager.newTemp();
        llvm.append("  ").append(arrayPtr)
                .append(" = bitcast i8* ").append(listTemp).append(" to %ArrayList*\n");


        String idxLLVM = node.getIndexNode().accept(visitor);
        String idxTemp;
        if (idxLLVM.contains(";;VAL:")) {
            idxTemp = extractTemp(idxLLVM);
            llvm.append(idxLLVM, 0, idxLLVM.lastIndexOf(";;VAL:")); // adiciona código do índice
        } else {
            idxTemp = tempManager.newTemp();
            llvm.append("  ").append(idxTemp)
                    .append(" = add i32 0, ").append(idxLLVM).append("\n");
        }

        String dynTemp = tempManager.newTemp();
        llvm.append("  ").append(dynTemp)
                .append(" = call %DynValue* @getItem(%ArrayList* ")
                .append(arrayPtr).append(", i32 ").append(idxTemp).append(")\n");

        // Marca VAL/TYPE
        llvm.append(";;VAL:").append(dynTemp).append("\n");
        llvm.append(";;TYPE:any\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int v = code.indexOf(";;VAL:");
        int t = code.indexOf(";;TYPE:", v);
        return code.substring(v + 6, t).trim();
    }
}