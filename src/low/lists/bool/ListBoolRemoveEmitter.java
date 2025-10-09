package low.lists.bool;

import ast.lists.ListRemoveNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;


public class ListBoolRemoveEmitter {
    private final TempManager tempManager;

    public ListBoolRemoveEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListRemoveNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();
        String listCode = node.getListNode().accept(visitor);
        appendCodePrefix(llvm,listCode);
        String listVal = extractValue(listCode);
        String idxCode = node.getIndexNode().accept(visitor);
        appendCodePrefix(llvm, idxCode);
        String idxValue = extractValue(idxCode);


        String idx64 = tempManager.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ").append(idxValue).append(" to i64\n");

        llvm.append("  call void @arraylist_remove_bool(%struct.ArrayListBool* ")
                .append(listVal).append(", i64 ").append(idx64).append(")\n");
        return llvm.toString();
    }


    // Adiciona o código antes do marcador ;;VAL:, ignorando metadados internos
    private void appendCodePrefix(StringBuilder llvm, String code) {
        // Encontra a posição do marcador ;;VAL:
        int marker = code.lastIndexOf(";;VAL:");
        // Se existe, pega tudo antes dele; caso contrário, pega o código inteiro
        String prefix = (marker == -1) ? code : code.substring(0, marker);
        // Garante que termina com quebra de linha
        if (!prefix.isEmpty()) {
            if (!prefix.endsWith("\n")) prefix += "\n";
            // Adiciona ao código final
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
