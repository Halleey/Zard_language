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
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;

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

        // Use elementType do node diretamente
        Type elemType = node.getElementType();
        System.out.println("basic debug for add " + elemType);
        // fallback seguro: tentar inferir do LLVMValue da lista
        if (elemType == null) {
            if (listVal.getType() instanceof LLVMArrayList arr) {
                LLVMTYPES el = arr.elementType();
                if (el instanceof LLVMInt) elemType = PrimitiveTypes.INT;
                else if (el instanceof LLVMDouble) elemType = PrimitiveTypes.DOUBLE;
                else if (el instanceof LLVMFloat) elemType = PrimitiveTypes.FLOAT;
                else if (el instanceof LLVMBool) elemType = PrimitiveTypes.BOOL;
                else if (el instanceof LLVMString) elemType = PrimitiveTypes.STRING;
                else return null;
            }
            if (elemType == null) {
                throw new RuntimeException(
                        "ListAddEmitter: tipo não suportado para add: null"
                );
            }
        }

        if (elemType instanceof PrimitiveTypes prim) {
            if (prim.equals(PrimitiveTypes.INT)) {
                LLVMValue added = intAddEmitter.emit(node, visitor);
                llvm.append(added.getCode());
                return added;
            } else if (prim.equals(PrimitiveTypes.DOUBLE)) {
                LLVMValue added = doubleEmitter.emit(node, visitor);
                llvm.append(added.getCode());
                return added;
            } else if (prim.equals(PrimitiveTypes.BOOL)) {
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