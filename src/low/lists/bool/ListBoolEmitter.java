package low.lists.bool;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMBool;

import java.util.List;
public class ListBoolEmitter {

    private final TempManager tempManager;

    public ListBoolEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public LLVMValue emit(ListNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        // Cria a lista
        String listPtr = tempManager.newTemp();
        LLVMArrayList listType = new LLVMArrayList(new LLVMBool());

        llvm.append("  ").append(listPtr)
                .append(" = call ").append(listType).append("* @arraylist_create_bool(i64 ")
                .append(Math.max(4, n)).append(")\n");

        if (n > 0) {
            String tempArray = tempManager.newTemp();
            llvm.append("  ").append(tempArray)
                    .append(" = alloca i8, i64 ").append(n).append("\n");

            for (int i = 0; i < n; i++) {
                ASTNode element = elements.get(i);
                LLVMValue elemVal = element.accept(visitor);

                // Código do elemento
                llvm.append(elemVal.getCode());

                // Checa tipo
                if (!(elemVal.getType() instanceof LLVMBool)) {
                    throw new RuntimeException("List<boolean> expected i1 element, got " + elemVal.getType());
                }

                // GEP + zext
                String gep = tempManager.newTemp();
                llvm.append("  ").append(gep)
                        .append(" = getelementptr inbounds i8, i8* ")
                        .append(tempArray).append(", i64 ").append(i).append("\n");

                String cast = tempManager.newTemp();
                llvm.append("  ").append(cast)
                        .append(" = zext i1 ").append(elemVal.getName()).append(" to i8\n");

                llvm.append("  store i8 ").append(cast)
                        .append(", i8* ").append(gep).append("\n");
            }

            llvm.append("  call void @arraylist_addAll_bool(")
                    .append(listType).append("* ").append(listPtr)
                    .append(", i8* ").append(tempArray)
                    .append(", i64 ").append(n).append(")\n");
        }

        return new LLVMValue(listType, listPtr, llvm.toString());
    }
}