package low.lists.bool;

import ast.lists.ListClearNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMInt;

public class ListBoolClearEmitter {
    private final TempManager tempManager;
    public ListBoolClearEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }
    public LLVMValue emit(ListClearNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getListNode().accept(visitor);
        llvm.append(listVal.getCode());

        llvm.append("  call void @arraylist_clear_bool(%struct.ArrayListBool* ")
                .append(listVal.getName())
                .append(")\n");
        return new LLVMValue(new LLVMArrayList(new LLVMInt()), listVal.getName(), llvm.toString());
    }

}
