package low.lists.doubles;

import ast.lists.ListAddAllNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMInt;

public class ListAddAllDoubleEmitter {
    private final TempManager temps;

    public ListAddAllDoubleEmitter(TempManager temps) {
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
                .append(" = alloca double, i64 ").append(n).append("\n");

        for (int i = 0; i < n; i++) {
            LLVMValue val = node.getArgs().get(i).accept(visitor);
            llvm.append(val.getCode());

            String gepTmp = temps.newTemp();
            llvm.append("  ").append(gepTmp)
                    .append(" = getelementptr inbounds double, double* ")
                    .append(tmpArray).append(", i64 ").append(i).append("\n");

            llvm.append("  store double ").append(val.getName())
                    .append(", double* ").append(gepTmp).append("\n");
        }

        llvm.append("  call void @arraylist_addAll_double(%struct.ArrayListDouble* ")
                .append(listTmp)
                .append(", double* ").append(tmpArray)
                .append(", i64 ").append(n).append(")\n");

        return new LLVMValue(
                new LLVMArrayList(new LLVMInt()),
                listTmp,
                llvm.toString()
        );
    }

}