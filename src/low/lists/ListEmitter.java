package low.lists;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVisitorMain;

import java.util.List;

public class ListEmitter {
    private final TempManager temps;

    public ListEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        // Cria a lista (ArrayList* -> i8*)
        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr)
                .append(" = call i8* @arraylist_create(i64 ").append(Math.max(4, n)).append(")\n");

        if (n == 0) {
            llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");
            return llvm.toString();
        }

        // Preenche a lista elemento a elemento
        for (ASTNode element : elements) {
            String elemLLVM = element.accept(visitor);
            String tmp = extractTemp(elemLLVM);
            String type = extractType(elemLLVM);

            llvm.append(elemLLVM);
            llvm.append("  call void @arraylist_add(i8* ").append(listPtr)
                    .append(", ").append(type).append(" ").append(tmp).append(")\n");
        }

        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");
        return llvm.toString();
    }

    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        int endIdx = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, endIdx).trim();
    }

    private String extractType(String code) {
        int idx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", idx);
        return code.substring(idx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
