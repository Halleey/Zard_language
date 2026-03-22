package low.lists.ints;


import ast.lists.ListGetNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMInt;

public class ListGetIntEmitter {

    private final TempManager temps;

    public ListGetIntEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(ListGetNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        // ===== LIST =====
        LLVMValue listVal = node.getListName().accept(visitor);
        llvm.append(listVal.getCode());

        // ===== INDEX =====
        LLVMValue idxVal = node.getIndexNode().accept(visitor);
        llvm.append(idxVal.getCode());

        String idx64 = temps.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ")
                .append(idxVal.getName())
                .append(" to i64\n");

        // ===== OUT PTR =====
        String outPtr = temps.newTemp();
        llvm.append("  ").append(outPtr)
                .append(" = alloca i32\n");

        // ===== CALL =====
        String success = temps.newTemp();
        llvm.append("  ").append(success)
                .append(" = call i32 @arraylist_get_int(%struct.ArrayListInt* ")
                .append(listVal.getName())
                .append(", i64 ").append(idx64)
                .append(", i32* ").append(outPtr)
                .append(")\n");

        // ===== LOAD =====
        String value = temps.newTemp();
        llvm.append("  ").append(value)
                .append(" = load i32, i32* ").append(outPtr).append("\n");

        return new LLVMValue(
                new LLVMInt(),
                value,
                llvm.toString()
        );
    }
}