package low.lists.bool;

import ast.lists.ListGetNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMBool;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;

public class ListBoolGetEmitter {

    private final TempManager temps;

    public ListBoolGetEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListGetNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getListName().accept(visitor);
        llvm.append(listVal.getCode());

        LLVMValue idxVal = node.getIndexNode().accept(visitor);
        llvm.append(idxVal.getCode());

        String idx64 = temps.newTemp();

        // conversão segura baseada no tipo real
        if (idxVal.getType() instanceof LLVMInt) {
            llvm.append("  ").append(idx64)
                    .append(" = zext i32 ")
                    .append(idxVal.getName())
                    .append(" to i64\n");

        }
        else {
            throw new RuntimeException("Unsupported index type for ListGetBool: " + idxVal.getType());
        }

        String outPtr = temps.newTemp();
        llvm.append("  ").append(outPtr)
                .append(" = alloca i1\n");

        String success = temps.newTemp();
        llvm.append("  ").append(success)
                .append(" = call i1 @arraylist_get_bool(%struct.ArrayListBool* ")
                .append(listVal.getName())
                .append(", i64 ").append(idx64)
                .append(", i1* ").append(outPtr)
                .append(")\n");

        String value = temps.newTemp();
        llvm.append("  ").append(value)
                .append(" = load i1, i1* ").append(outPtr).append("\n");

        return new LLVMValue(new LLVMBool(), value, llvm.toString());
    }
}