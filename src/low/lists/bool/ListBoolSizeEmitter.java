package low.lists.bool;

import ast.lists.ListSizeNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMInt;

public class ListBoolSizeEmitter {

    private final TempManager temps;

    public ListBoolSizeEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListSizeNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getNome().accept(visitor);
        llvm.append(listVal.getCode());

        String sizeTmp = temps.newTemp();
        llvm.append("  ").append(sizeTmp)
                .append(" = call i32 @arraylist_size_bool(%struct.ArrayListBool* ")
                .append(listVal.getName())
                .append(")\n");

        return new LLVMValue(
                new LLVMInt(),
                sizeTmp,
                llvm.toString()
        );
    }
}