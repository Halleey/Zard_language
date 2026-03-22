package low.lists.ints;

import ast.lists.ListAddAllNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMInt;

public class ListIntAddAllEmitter {

    private final TempManager temps;

    public ListIntAddAllEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListAddAllNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getTargetListNode().accept(visitor);
        llvm.append(listVal.getCode());
        String listTmp = listVal.getName();

        int n = node.getArgs().size();
        if (n == 0) return listVal;

        String tmpArray = temps.newTemp();
        llvm.append("  ").append(tmpArray)
                .append(" = alloca i32, i64 ").append(n).append("\n");

        for (int i = 0; i < n; i++) {
            LLVMValue val = node.getArgs().get(i).accept(visitor);
            llvm.append(val.getCode());

            String gepTmp = temps.newTemp();
            llvm.append("  ").append(gepTmp)
                    .append(" = getelementptr inbounds i32, i32* ")
                    .append(tmpArray).append(", i64 ").append(i).append("\n");

            llvm.append("  store i32 ").append(val.getName())
                    .append(", i32* ").append(gepTmp).append("\n");
        }

        llvm.append("  call void @arraylist_addAll_int(%struct.ArrayListInt* ")
                .append(listTmp)
                .append(", i32* ").append(tmpArray)
                .append(", i64 ").append(n).append(")\n");

        return new LLVMValue(
                new LLVMArrayList(new LLVMInt()),
                listTmp,
                llvm.toString()
        );
    }
}