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

        StringBuilder llvm = new StringBuilder();

        // LIST
        LLVMValue listVal = node.getNome().accept(visitor);
        llvm.append(listVal.getCode());

        String tmp = temps.newTemp();

        //  DISPATCH POR TIPO REAL DA LISTA
        if (listVal.getType() instanceof LLVMArrayList arr) {

            if (arr.elementType() instanceof LLVMInt) {
                llvm.append("  ").append(tmp)
                        .append(" = call i32 @arraylist_size_int(%struct.ArrayListInt* ")
                        .append(listVal.getName())
                        .append(")\n");

            } else {
                // fallback genérico
                llvm.append("  ").append(tmp)
                        .append(" = call i32 @length(%ArrayList* ")
                        .append(listVal.getName())
                        .append(")\n");
            }

        } else {
            throw new RuntimeException("ListSize em tipo inválido: " + listVal.getType());
        }

        return new LLVMValue(new LLVMInt(), tmp, llvm.toString());
    }
}