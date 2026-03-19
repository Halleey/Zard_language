package low.lists.generics;

import ast.lists.ListSizeNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.TempManager;
import low.lists.bool.ListBoolSizeEmitter;
import low.lists.doubles.ListDoubleSizeEmitter;
import low.lists.ints.ListIntSizeEmitter;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMInt;

public class ListSizeEmitter {

    private final TempManager temps;
    private final ListIntSizeEmitter intEmitter;
    private final ListDoubleSizeEmitter doubleEmitter;
    private final ListBoolSizeEmitter boolEmitter;

    public ListSizeEmitter(TempManager temps) {
        this.temps = temps;
        this.intEmitter = new ListIntSizeEmitter(temps);
        this.doubleEmitter = new ListDoubleSizeEmitter(temps);
        this.boolEmitter = new ListBoolSizeEmitter(temps);
    }

    public LLVMValue emit(ListSizeNode node, LLVisitorMain visitor) {

        Type elemType = node.getType();

        if (elemType instanceof PrimitiveTypes prim) {
            if (prim == PrimitiveTypes.INT) return intEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.DOUBLE) return doubleEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.BOOL) return boolEmitter.emit(node, visitor);
        }

        StringBuilder llvm = new StringBuilder();

        // ===== LIST =====
        LLVMValue listVal = node.getNome().accept(visitor);
        llvm.append(listVal.getCode());

        String listPtr = listVal.getName();

        // ===== CAST (se necessário) =====
        String listCast;

        if (listVal.getType() instanceof LLVMArrayList) {
            listCast = listPtr;
        } else {
            listCast = temps.newTemp();
            llvm.append("  ").append(listCast)
                    .append(" = bitcast i8* ")
                    .append(listPtr)
                    .append(" to %ArrayList*\n");
        }

        String sizeTmp = temps.newTemp();
        llvm.append("  ").append(sizeTmp)
                .append(" = call i32 @length(%ArrayList* ")
                .append(listCast)
                .append(")\n");

        return new LLVMValue(new LLVMInt(), sizeTmp, llvm.toString());
    }
}