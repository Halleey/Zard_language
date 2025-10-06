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

        for (ASTNode element : elements) {
            String elemLLVM = element.accept(visitor);
            llvm.append(elemLLVM);

            String temp = extractTemp(elemLLVM);
            String type = extractType(elemLLVM);

            if (!type.equals("i32")) {
                throw new RuntimeException("List<int> expected i32 element, got " + type);
            }

            llvm.append("  call void @arraylist_add_int(%struct.ArrayListInt* ")
                    .append(listPtr).append(", i32 ").append(temp).append(")\n");
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
