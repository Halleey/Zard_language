package low.lists.ints;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;

import java.util.List;

public class IntListEmitter {
    private final TempManager temps;

    public IntListEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        // Cria a lista
        String listPtr = temps.newTemp();
        LLVMArrayList listType = new LLVMArrayList(new LLVMDouble());

        llvm.append("  ").append(listPtr)
                .append(" = call ").append(listType).append("* @arraylist_create_int(i64 ")
                .append(Math.max(4, n)).append(")\n");

        if (n > 0) {
            String tempArray = temps.newTemp();
            llvm.append("  ").append(tempArray)
                    .append(" = alloca i32, i64 ").append(n).append("\n");

            for (int i = 0; i < n; i++) {
                ASTNode element = elements.get(i);

                LLVMValue elemVal = element.accept(visitor);
                llvm.append(elemVal.getCode());

                if (!(elemVal.getType() instanceof LLVMInt)) {
                    throw new RuntimeException("List<int> expected int element, got " + elemVal.getType());
                }

                // Cria GEP
                String gep = temps.newTemp();
                llvm.append("  ").append(gep)
                        .append(" = getelementptr inbounds i32, i32* ")
                        .append(tempArray).append(", i64 ").append(i).append("\n");

                // Armazena o valor
                llvm.append("  store i32 ").append(elemVal.getName())
                        .append(", i32* ").append(gep).append("\n");
            }

            llvm.append("  call void @arraylist_addAll_int(")
                    .append(listType).append("* ").append(listPtr)
                    .append(", i32* ").append(tempArray)
                    .append(", i64 ").append(n).append(")\n");
        }
        return new LLVMValue(listType, listPtr, llvm.toString());
    }

}
