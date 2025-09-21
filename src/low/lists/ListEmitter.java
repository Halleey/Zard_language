package low.lists;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;

public class ListEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public ListEmitter(TempManager temps, GlobalStringManager globalStrings) {
        this.temps = temps;
        this.globalStrings = globalStrings;
    }

    public String emit(ListNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        // cria a lista inicial
        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr).append(" = call i8* @arraylist_create(i64 4)\n");

        // adiciona elementos
        for (ASTNode element : node.getList().getElements()) {
            String exprLLVM = element.accept(visitor);
            String temp = extractTemp(exprLLVM);
            String type = extractType(exprLLVM);
            llvm.append(exprLLVM);
            llvm.append("  call void @setItems(i8* ").append(listPtr).append(", ")
                    .append(type).append(" ").append(temp).append(")\n");
        }

        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");
        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int typeIdx = code.indexOf(";;TYPE:");
        if (typeIdx == -1) throw new RuntimeException("Não encontrou ;;TYPE: em: " + code);
        int endIdx = code.indexOf("\n", typeIdx);
        return code.substring(typeIdx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
