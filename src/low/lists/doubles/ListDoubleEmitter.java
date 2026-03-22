package low.lists.doubles;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMDouble;


import java.util.List;

public class ListDoubleEmitter {
    private final TempManager tempManager;

    public ListDoubleEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }


    public LLVMValue emit(ListNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        // Cria a lista
        String listPtr = tempManager.newTemp();
        LLVMArrayList listType = new LLVMArrayList(new LLVMDouble());

        llvm.append("  ").append(listPtr)
                .append(" = call ").append(listType).append("* @arraylist_create_double(i64 ")
                .append(Math.max(4, n)).append(")\n");

        if (n > 0) {
            String tempArray = tempManager.newTemp();
            llvm.append("  ").append(tempArray)
                    .append(" = alloca double, i64 ").append(n).append("\n");

            for (int i = 0; i < n; i++) {
                ASTNode element = elements.get(i);

                LLVMValue elemVal = element.accept(visitor);
                llvm.append(elemVal.getCode());

                if (!(elemVal.getType() instanceof LLVMDouble)) {
                    throw new RuntimeException("List<double> expected double element, got " + elemVal.getType());
                }

                // Cria GEP
                String gep = tempManager.newTemp();
                llvm.append("  ").append(gep)
                        .append(" = getelementptr inbounds double, double* ")
                        .append(tempArray).append(", i64 ").append(i).append("\n");

                // Armazena o valor
                llvm.append("  store double ").append(elemVal.getName())
                        .append(", double* ").append(gep).append("\n");
            }

            llvm.append("  call void @arraylist_addAll_double(")
                    .append(listType).append("* ").append(listPtr)
                    .append(", double* ").append(tempArray)
                    .append(", i64 ").append(n).append(")\n");
        }
        return new LLVMValue(listType, listPtr, llvm.toString());
    }
}