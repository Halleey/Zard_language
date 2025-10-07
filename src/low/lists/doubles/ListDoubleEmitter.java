package low.lists.doubles;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class ListDoubleEmitter {
    private final TempManager tempManager;

    public ListDoubleEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emiter(ListNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();
        String listPtr =  tempManager.newTemp();
        llvm.append("  ").append(listPtr).append(" = call %struct.ArrayListDouble* @arraylist_create_double(i64 ")
                .append(Math.max(4, n)).append(")\n");

        for (ASTNode element: elements){
            String elemLLVM  = element.accept(visitor);
            llvm.append(elemLLVM);
            String temp = extractTemp(elemLLVM);
            String type = extractType(elemLLVM);
            if (!type.equals("double")) {
                throw new RuntimeException("List<double> expected double element, got " + type);
            }

            llvm.append("  call void @arraylist_add_double(%struct.ArrayListDouble* ")
                    .append(listPtr)
                    .append(", double ")
                    .append(temp)
                    .append(")\n");
        }
        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:%struct.ArrayListDouble*\n");
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
