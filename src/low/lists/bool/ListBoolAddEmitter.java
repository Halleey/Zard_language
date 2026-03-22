package low.lists.bool;

import ast.lists.ListAddNode;
import context.statics.symbols.PrimitiveTypes;
import low.TempManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMBool;


public class ListBoolAddEmitter {

    private final TempManager temps;

    public ListBoolAddEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListAddNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getListNode().accept(visitor);
        llvm.append(listVal.getCode());

        LLVMValue val = node.getValuesNode().accept(visitor);
        llvm.append(val.getCode());

        llvm.append("  call void @arraylist_add_bool(%struct.ArrayListBool* ")
                .append(listVal.getName())
                .append(", i1 ")
                .append(val.getName())
                .append(")\n");

        return new LLVMValue(new LLVMArrayList(new LLVMBool()), listVal.getName(), llvm.toString());
    }
}
