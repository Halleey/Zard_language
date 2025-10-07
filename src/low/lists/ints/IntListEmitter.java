package low.lists.ints;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVisitorMain;

import java.util.List;

public class IntListEmitter {
    private final TempManager temps;

    public IntListEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr)
                .append(" = call %struct.ArrayListInt* @arraylist_create_int(i64 ")
                .append(Math.max(4, n)).append(")\n");

        if (n > 0) {
            // Cria array temporário na stack
            String tempArray = temps.newTemp();
            llvm.append("  ").append(tempArray)
                    .append(" = alloca i32, i64 ").append(n).append("\n");

            // Preenche array temporário
            for (int i = 0; i < n; i++) {
                ASTNode element = elements.get(i);
                String elemLLVM = element.accept(visitor);
                llvm.append(elemLLVM);

                String temp = extractTemp(elemLLVM);
                String type = extractType(elemLLVM);
                if (!type.equals("i32")) {
                    throw new RuntimeException("List<int> expected i32 element, got " + type);
                }

                String gep = temps.newTemp();
                llvm.append("  ").append(gep)
                        .append(" = getelementptr inbounds i32, i32* ").append(tempArray)
                        .append(", i64 ").append(i).append("\n");

                llvm.append("  store i32 ").append(temp).append(", i32* ").append(gep).append("\n");
            }

            // Chamada única addAll
            llvm.append("  call void @arraylist_addAll_int(%struct.ArrayListInt* ")
                    .append(listPtr).append(", i32* ").append(tempArray)
                    .append(", i64 ").append(n).append(")\n");
        }

        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:%struct.ArrayListInt*\n");
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
