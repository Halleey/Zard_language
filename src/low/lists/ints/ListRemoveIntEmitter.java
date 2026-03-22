package low.lists.ints;

import ast.lists.ListRemoveNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMVoid;

public class ListRemoveIntEmitter {

    private final TempManager temps;

    public ListRemoveIntEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListRemoveNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getListNode().accept(visitor);
        llvm.append(listVal.getCode());

        LLVMValue idxVal = node.getIndexNode().accept(visitor);
        llvm.append(idxVal.getCode());

        String idx64 = temps.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ")
                .append(idxVal.getName())
                .append(" to i64\n");

        llvm.append("  call void @arraylist_remove_int(%struct.ArrayListInt* ")
                .append(listVal.getName())
                .append(", i64 ")
                .append(idx64)
                .append(")\n");

        return new LLVMValue(
                new LLVMVoid(),
                "",
                llvm.toString()
        );
    }
}