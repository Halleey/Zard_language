package low.lists.generics;

import ast.lists.ListRemoveNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.TempManager;
import low.lists.bool.ListBoolRemoveEmitter;
import low.lists.doubles.ListDoubleRemoveEmitter;
import low.lists.ints.ListRemoveIntEmitter;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMInt;
import low.module.builders.primitives.LLVMVoid;
public class ListRemoveEmitter {

    private final TempManager temps;
    private final ListRemoveIntEmitter intEmitter;
    private final ListDoubleRemoveEmitter doubleEmitter;
    private final ListBoolRemoveEmitter boolEmitter;

    public ListRemoveEmitter(TempManager temps) {
        this.temps = temps;
        this.intEmitter = new ListRemoveIntEmitter(temps);
        this.doubleEmitter = new ListDoubleRemoveEmitter(temps);
        this.boolEmitter = new ListBoolRemoveEmitter(temps);
    }

    public LLVMValue emit(ListRemoveNode node, LLVisitorMain visitor) {

        // ===== DISPATCH PRIMEIRO (igual ListEmitter) =====
        Type elemType = node.getType();

        if (elemType instanceof PrimitiveTypes prim) {
            if (prim == PrimitiveTypes.INT) return intEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.DOUBLE) return doubleEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.BOOL) return boolEmitter.emit(node, visitor);
        }

        StringBuilder llvm = new StringBuilder();

        // ===== LIST =====
        LLVMValue listVal = node.getListNode().accept(visitor);
        llvm.append(listVal.getCode());

        // ===== INDEX =====
        LLVMValue idxVal = node.getIndexNode().accept(visitor);
        llvm.append(idxVal.getCode());

        if (!(idxVal.getType() instanceof LLVMInt)) {
            throw new RuntimeException(
                    "ListRemove: index must be int, got: " + idxVal.getType()
            );
        }

        String idx64 = temps.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ")
                .append(idxVal.getName())
                .append(" to i64\n");

        // ===== GENERIC REMOVE =====
        if (listVal.getType() instanceof LLVMArrayList) {

            llvm.append("  call void @removeItem(%ArrayList* ")
                    .append(listVal.getName())
                    .append(", i64 ")
                    .append(idx64)
                    .append(")\n");

        } else {

            String cast = temps.newTemp();

            llvm.append("  ").append(cast)
                    .append(" = bitcast i8* ")
                    .append(listVal.getName())
                    .append(" to %ArrayList*\n");

            llvm.append("  call void @removeItem(%ArrayList* ")
                    .append(cast)
                    .append(", i64 ")
                    .append(idx64)
                    .append(")\n");
        }
        return new LLVMValue(new LLVMVoid(), "", llvm.toString());
    }
}