package low.lists.doubles;

import ast.lists.ListAddNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMDouble;

public class ListAddDoubleEmitter {

    private final TempManager temps;

    public ListAddDoubleEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListAddNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getListNode().accept(visitor);
        llvm.append(listVal.getCode());

        LLVMValue val = node.getValuesNode().accept(visitor);
        llvm.append(val.getCode());

        llvm.append("  call void @arraylist_add_double(%struct.ArrayListDouble* ")
                .append(listVal.getName())
                .append(", double ")
                .append(val.getName())
                .append(")\n");

        return new LLVMValue(new LLVMArrayList(new LLVMDouble()), listVal.getName(), llvm.toString());
    }
}