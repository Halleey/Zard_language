package low.lists;

import ast.lists.ListGetNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
public class ListGetEmitter {
    private final TempManager temps;

    public ListGetEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListGetNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        // Gera código para a lista
        String listCode = node.getListName().accept(visitor);
        String listTemp = extractTemp(listCode);
        appendCodePrefix(llvm, listCode);

        // Converte i8* para %ArrayList*
        String arrayPtr = temps.newTemp();
        llvm.append("  ").append(arrayPtr)
                .append(" = bitcast i8* ").append(listTemp)
                .append(" to %ArrayList*\n");

        // Gera código para o índice
        String idxLLVM = node.getIndexNode().accept(visitor);
        String idxTemp;
        if (idxLLVM.contains(";;VAL:")) {
            idxTemp = extractTemp(idxLLVM);
            llvm.append(idxLLVM, 0, idxLLVM.lastIndexOf(";;VAL:")); // inclui código do índice
        } else {
            idxTemp = temps.newTemp();
            llvm.append("  ").append(idxTemp)
                    .append(" = add i32 0, ").append(idxLLVM).append("\n");
        }

        // Promove índice para i64
        String idx64 = temps.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ").append(idxTemp).append(" to i64\n");

        // Chama getItem (retorna elemento)
        String valTemp = temps.newTemp();
        llvm.append("  ").append(valTemp)
                .append(" = call i8* @getItem(%ArrayList* ").append(arrayPtr)
                .append(", i64 ").append(idx64).append(")\n");

        // Marca VAL/TYPE
        llvm.append(";;VAL:").append(valTemp).append("\n");
        llvm.append(";;TYPE:i8*\n"); // indica que é um elemento individual

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
        int v = code.indexOf(";;VAL:");
        int t = code.indexOf(";;TYPE:", v);
        return code.substring(v + 6, t).trim();
    }
}
