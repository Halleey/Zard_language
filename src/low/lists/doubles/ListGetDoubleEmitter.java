package low.lists.doubles;

import ast.lists.ListGetNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMBool;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;

public class ListGetDoubleEmitter {

    private final TempManager temps;

    public ListGetDoubleEmitter(TempManager temps) {
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

        if (idxVal.getType() instanceof LLVMInt) {

            llvm.append("  ").append(idx64)
                    .append(" = zext i32 ")
                    .append(idxVal.getName())
                    .append(" to i64\n");

        }  else {
            throw new RuntimeException(
                    "Unsupported index type for ListGetDouble: " + idxVal.getType()
            );
        }

        // ===== OUT PTR =====
        String outPtr = temps.newTemp();
        llvm.append("  ").append(outPtr)
                .append(" = alloca double\n");

        // ===== CALL =====
        String got = temps.newTemp();
        llvm.append("  ").append(got)
                .append(" = call double @arraylist_get_double(%struct.ArrayListDouble* ")
                .append(listVal.getName())
                .append(", i64 ").append(idx64)
                .append(", double* ").append(outPtr)
                .append(")\n");

        // ===== LOAD =====
        String value = temps.newTemp();
        llvm.append("  ").append(value)
                .append(" = load double, double* ").append(outPtr).append("\n");

        return new LLVMValue(
                new LLVMDouble(),
                value,
                llvm.toString()
        );
    }
}