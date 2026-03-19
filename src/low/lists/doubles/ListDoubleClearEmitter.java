package low.lists.doubles;

import ast.lists.ListClearNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMInt;

public class ListDoubleClearEmitter {

    private final TempManager temps;

    public ListDoubleClearEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListClearNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getListNode().accept(visitor);
        llvm.append(listVal.getCode());

        llvm.append("  call void @arraylist_clear_double(%struct.ArrayListDouble* ")
                .append(listVal.getName())
                .append(")\n");

        return new LLVMValue(new LLVMArrayList(new LLVMInt()), listVal.getName(), llvm.toString());
    }
}