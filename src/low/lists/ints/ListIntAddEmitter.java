package low.lists.ints;

import ast.lists.ListAddNode;
import context.statics.symbols.PrimitiveTypes;
import low.TempManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMInt;


public class ListIntAddEmitter {

    private final TempManager temps;

    public ListIntAddEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListAddNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getListNode().accept(visitor);
        llvm.append(listVal.getCode());

        LLVMValue val = node.getValuesNode().accept(visitor);
        llvm.append(val.getCode());

        llvm.append("  call void @arraylist_add_int(%struct.ArrayListInt* ")
                .append(listVal.getName())
                .append(", i32 ")
                .append(val.getName())
                .append(")\n");

        return new LLVMValue(new LLVMArrayList(new LLVMInt()), listVal.getName(), llvm.toString());
    }
}