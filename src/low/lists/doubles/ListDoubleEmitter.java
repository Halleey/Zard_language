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

    public String emit(ListNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        String listPtr = tempManager.newTemp();
        llvm.append("  ").append(listPtr)
                .append(" = call %struct.ArrayListDouble* @arraylist_create_double(i64 ")
                .append(Math.max(4, n)).append(")\n");

        if (n > 0) {
            String tempArray = tempManager.newTemp();
            llvm.append("  ").append(tempArray)
                    .append(" = alloca double, i64 ").append(n).append("\n");

            for (int i = 0; i < n; i++) {
                ASTNode element = elements.get(i);
                String elemLLVM = element.accept(visitor);
                llvm.append(elemLLVM);

                String temp = extractTemp(elemLLVM);
                String type = extractType(elemLLVM);
                if (!type.equals("double")) {
                    throw new RuntimeException("List<double> expected double element, got " + type);
                }

                String gep = tempManager.newTemp();
                llvm.append("  ").append(gep)
                        .append(" = getelementptr inbounds double, double* ")
                        .append(tempArray).append(", i64 ").append(i).append("\n");

                llvm.append("  store double ").append(temp).append(", double* ").append(gep).append("\n");
            }

            llvm.append("  call void @arraylist_addAll_double(%struct.ArrayListDouble* ")
                    .append(listPtr).append(", double* ").append(tempArray)
                    .append(", i64 ").append(n).append(")\n");
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
