package low.lists.generics;

import ast.ASTNode;
import ast.lists.ListNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.lists.bool.ListBoolEmitter;
import low.lists.doubles.ListDoubleEmitter;
import low.lists.ints.IntListEmitter;
import low.lists.string.ListStringEmitter;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.structs.LLVMStruct;

import java.util.List;

public class ListEmitter {

    private final TempManager temps;
    private final IntListEmitter intEmitter;
    private final ListDoubleEmitter doubleEmitter;
    private final ListBoolEmitter boolEmitter;
    private final ListStringEmitter stringEmitter;

    public ListEmitter(TempManager temps) {
        this.temps = temps;
        this.intEmitter = new IntListEmitter(temps);
        this.doubleEmitter = new ListDoubleEmitter(temps);
        this.boolEmitter = new ListBoolEmitter(temps);
        this.stringEmitter = new ListStringEmitter(temps);
    }

    public LLVMValue emit(ListNode node, LLVisitorMain visitor) {

        Type elementType = node.getList().getElementType();

        // primitivas
        if (elementType instanceof PrimitiveTypes prim) {
            if (prim == PrimitiveTypes.INT) return intEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.DOUBLE) return doubleEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.BOOL) return boolEmitter.emit(node, visitor);
            if (prim == PrimitiveTypes.STRING) return stringEmitter.emit(node, visitor);
        }

        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        StringBuilder llvm = new StringBuilder();

        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr)
                .append(" = call i8* @arraylist_create(i64 ")
                .append(Math.max(4, n))
                .append(")\n");

        String listCast = temps.newTemp();
        llvm.append("  ").append(listCast)
                .append(" = bitcast i8* ").append(listPtr)
                .append(" to %ArrayList*\n");

        // structs ou tipos complexos
        if (elementType instanceof StructType st) {
            LLVMStruct structType = new LLVMStruct(st.name());
            LLVMArrayList listType = new LLVMArrayList(structType); // elementType conhecido, mas não usado no LLVM IR

            for (ASTNode element : elements) {
                LLVMValue elemVal = element.accept(visitor);
                llvm.append(elemVal.getCode());

                llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                        .append(listCast)
                        .append(", ")
                        .append(elemVal.getType())
                        .append(" ")
                        .append(elemVal.getName())
                        .append(")\n");
            }
            return new LLVMValue(listType, listCast, llvm.toString());
        }

        throw new RuntimeException(
                "Unsupported list element type: " + elementType
        );
    }
}