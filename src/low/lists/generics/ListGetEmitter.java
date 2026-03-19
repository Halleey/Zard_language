package low.lists.generics;

import ast.lists.ListGetNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.lists.bool.ListBoolGetEmitter;
import low.lists.doubles.ListGetDoubleEmitter;
import low.lists.ints.ListGetIntEmitter;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMBool;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;
import low.module.builders.primitives.LLVMString;
import low.module.builders.structs.LLVMStruct;

public class ListGetEmitter {

    private final TempManager temps;
    private final ListGetIntEmitter intGetEmitter;
    private final ListGetDoubleEmitter doubleEmitter;
    private final ListBoolGetEmitter boolGetEmitter;

    public ListGetEmitter(TempManager temps) {
        this.temps = temps;
        this.intGetEmitter = new ListGetIntEmitter(temps);
        this.doubleEmitter = new ListGetDoubleEmitter(temps);
        this.boolGetEmitter = new ListBoolGetEmitter(temps);
    }

    public LLVMValue emit(ListGetNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        LLVMValue listVal = node.getListName().accept(visitor);
        llvm.append(listVal.getCode());

        LLVMTYPES listType = listVal.getType();

        if (!(listType instanceof LLVMArrayList arrayListType)) {
            throw new RuntimeException("ListGetEmitter: expected LLVMArrayList, got " + listType);
        }

        LLVMTYPES elementType = arrayListType.elementType();
        Type elemType = node.getElementType();

        if (elemType instanceof PrimitiveTypes prim) {
            if (prim == PrimitiveTypes.INT) return intGetEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.DOUBLE) return doubleEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.BOOL) return boolGetEmitter.emit(node, visitor);
        }

        // ===== INDEX =====
        LLVMValue idxVal = node.getIndexNode().accept(visitor);
        llvm.append(idxVal.getCode());

        String idx64 = temps.newTemp();
        llvm.append("  ")
                .append(idx64)
                .append(" = zext i32 ")
                .append(idxVal.getName())
                .append(" to i64\n");

        // ===== RAW GET =====
        String rawTemp = temps.newTemp();
        llvm.append("  ")
                .append(rawTemp)
                .append(" = call i8* @arraylist_get_ptr(%ArrayList* ")
                .append(listVal.getName())
                .append(", i64 ")
                .append(idx64)
                .append(")\n");

        // ===== PRIMITIVOS =====
        if (elementType instanceof LLVMString) {
            return new LLVMValue(elementType, rawTemp, llvm.toString());
        }

        if (elementType instanceof LLVMInt) {

            String ptr = temps.newTemp();
            llvm.append("  ").append(ptr)
                    .append(" = bitcast i8* ").append(rawTemp).append(" to i32*\n");

            String val = temps.newTemp();
            llvm.append("  ").append(val)
                    .append(" = load i32, i32* ").append(ptr).append("\n");

            return new LLVMValue(elementType, val, llvm.toString());
        }

        if (elementType instanceof LLVMDouble) {

            String ptr = temps.newTemp();
            llvm.append("  ").append(ptr)
                    .append(" = bitcast i8* ").append(rawTemp).append(" to double*\n");

            String val = temps.newTemp();
            llvm.append("  ").append(val)
                    .append(" = load double, double* ").append(ptr).append("\n");

            return new LLVMValue(elementType, val, llvm.toString());
        }

        if (elementType instanceof LLVMBool) {

            String ptr = temps.newTemp();
            llvm.append("  ").append(ptr)
                    .append(" = bitcast i8* ").append(rawTemp).append(" to i1*\n");

            String val = temps.newTemp();
            llvm.append("  ").append(val)
                    .append(" = load i1, i1* ").append(ptr).append("\n");

            return new LLVMValue(elementType, val, llvm.toString());
        }

        // ===== STRUCT =====
        if (elementType instanceof LLVMStruct struct) {

            String castTemp = temps.newTemp();

            llvm.append("  ")
                    .append(castTemp)
                    .append(" = bitcast i8* ")
                    .append(rawTemp)
                    .append(" to %")
                    .append(struct.getName())
                    .append("*\n");

            return new LLVMValue(struct, castTemp, llvm.toString());
        }

        throw new RuntimeException("Unsupported list element type: " + elementType);
    }
}