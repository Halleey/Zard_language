package low.lists.generics;

import ast.lists.ListAddNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.TempManager;
import low.lists.bool.ListBoolAddEmitter;
import low.lists.doubles.ListAddDoubleEmitter;
import low.lists.ints.ListIntAddEmitter;
import low.main.GlobalStringManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMString;

import static context.statics.symbols.PrimitiveTypes.*;

public class ListAddEmitter {

    private final TempManager temps;
    private final GlobalStringManager globalStringManager;
    private final ListIntAddEmitter intAddEmitter;
    private final ListAddDoubleEmitter doubleEmitter;
    private final ListBoolAddEmitter boolAddEmitter;

    public ListAddEmitter(TempManager temps, GlobalStringManager globalStringManager) {
        this.temps = temps;
        this.globalStringManager = globalStringManager;
        this.intAddEmitter = new ListIntAddEmitter(temps);
        this.doubleEmitter = new ListAddDoubleEmitter(temps);
        this.boolAddEmitter = new ListBoolAddEmitter(temps);
    }

    public LLVMValue emit(ListAddNode node, LLVisitorMain visitor) {

        LLVMValue listVal = node.getListNode().accept(visitor);
        LLVMValue val = node.getValuesNode().accept(visitor);

        StringBuilder llvm = new StringBuilder();
        llvm.append(listVal.getCode());
        llvm.append(val.getCode());

        Type elemType = node.getType();

        // ==== Specialization via primitive type ====
        if (elemType instanceof PrimitiveTypes prim) {
            if (prim.equals(INT)) {
                LLVMValue added = intAddEmitter.emit(node, visitor);
                llvm.append(added.getCode());
                return added;
            } else if (prim.equals(DOUBLE)) {
                LLVMValue added = doubleEmitter.emit(node, visitor);
                llvm.append(added.getCode());
                return added;
            } else if (prim.equals(BOOL)) {
                LLVMValue added = boolAddEmitter.emit(node, visitor);
                llvm.append(added.getCode());
                return added;
            }
        }

        if (elemType == PrimitiveTypes.STRING) {
            String listName = listVal.getName();
            String valName = val.getName();
            String listTypeLLVM = "%ArrayListString*";

            if (!(listVal.getType() instanceof LLVMArrayList && ((LLVMArrayList) listVal.getType()).elementType() instanceof LLVMString)) {
                String castList = temps.newTemp();
                llvm.append("  ").append(castList)
                        .append(" = bitcast ").append(listVal.getType().toString())
                        .append(" ").append(listName)
                        .append(" to ").append(listTypeLLVM).append("\n");
                listName = castList;
            }

            llvm.append("  call void @arraylist_string_add(%ArrayListString* ")
                    .append(listName)
                    .append(", %String* ").append(valName)
                    .append(")\n");

            return new LLVMValue(new LLVMArrayList(new LLVMString()), listName, llvm.toString());
        }

        // ==== Generic fallback for pointers ====
        String listName = listVal.getName();
        if (!(listVal.getType() instanceof LLVMArrayList)) {
            String castList = temps.newTemp();
            llvm.append("  ").append(castList)
                    .append(" = bitcast ").append(listVal.getType().toString())
                    .append(" ").append(listName)
                    .append(" to %ArrayList*\n");
            listName = castList;
        }

        String valName = val.getName();
        if (!val.getType().equals(new LLVMPointer(null))) { // qualquer pointer genérico
            String castVal = temps.newTemp();
            llvm.append("  ").append(castVal)
                    .append(" = bitcast ").append(val.getType().toString())
                    .append(" ").append(valName)
                    .append(" to i8*\n");
            valName = castVal;
        }

        llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                .append(listName)
                .append(", i8* ").append(valName)
                .append(")\n");

        return new LLVMValue(new LLVMArrayList(null), listName, llvm.toString());
    }
}