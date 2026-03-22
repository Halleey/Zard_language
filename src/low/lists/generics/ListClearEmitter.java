package low.lists.generics;

import ast.lists.ListClearNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.TempManager;
import low.lists.bool.ListBoolClearEmitter;
import low.lists.doubles.ListDoubleClearEmitter;
import low.lists.ints.ListIntClearEmitter;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMVoid;

public class ListClearEmitter {

    private final TempManager temps;
    private final ListIntClearEmitter listIntClearEmitter;
    private final ListDoubleClearEmitter doubleClearEmitter;
    private final ListBoolClearEmitter boolClearEmitter;

    public ListClearEmitter(TempManager temps) {
        this.temps = temps;
        this.listIntClearEmitter = new ListIntClearEmitter(temps);
        this.doubleClearEmitter = new ListDoubleClearEmitter(temps);
        this.boolClearEmitter = new ListBoolClearEmitter(temps);
    }

    public LLVMValue emit(ListClearNode node, LLVisitorMain visitor) {

        LLVMValue listVal = node.getListNode().accept(visitor);
        StringBuilder llvm = new StringBuilder();
        llvm.append(listVal.getCode());

        Type elemType = node.getType();

        if (elemType instanceof PrimitiveTypes prim) {
            if (prim == PrimitiveTypes.INT) return listIntClearEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.DOUBLE) return doubleClearEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.BOOL) return boolClearEmitter.emit(node, visitor);
        }

        // Generic ArrayList clear
        LLVMValue castedList = listVal;
        if (!(listVal.getType() instanceof LLVMArrayList)) {
            String castName = temps.newTemp();
            llvm.append("  ").append(castName)
                    .append(" = bitcast ").append(listVal.getType().toString())
                    .append(" ").append(listVal.getName())
                    .append(" to %ArrayList*\n");
            castedList = new LLVMValue(new LLVMArrayList(null), castName, "");
        }

        llvm.append("  call void @clearList(%ArrayList* ").append(castedList.getName()).append(")\n");

        return new LLVMValue(new LLVMVoid(), castedList.getName(), llvm.toString());
    }
}