package low.lists.bool;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class ListBoolEmitter {
    private final TempManager tempManager;

    public ListBoolEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emiter(ListNode node, LLVMEmitVisitor visitor){
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        String listPtr = tempManager.newTemp();
        llvm.append("  ").append(listPtr);
        llvm.append(" = call %struct.ArrayListBool* @arraylist_create_bool(i64 ").append(Math.max(4, n)).append(")\n");

        for (ASTNode element: elements) {
            String elemLLVM = element.accept(visitor);
            llvm.append(elemLLVM);

            String type = extractType(elemLLVM);
            String tmp = extractTemp(elemLLVM);

            if (!type.equals("i1")) {
                throw new RuntimeException("List<boolean> expected i1 element, got " + type);
            }
            llvm.append("  call void @arraylist_add_bool(%struct.ArrayListBool* ")
                    .append(listPtr).append(", i1").append(tmp).append(")\n");
        }
        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:%struct.ArrayListBool*\n");
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

