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

    public String emit(ListNode node, LLVisitorMain visitor) {

        Type elementType = node.getList().getElementType();

        if (elementType instanceof PrimitiveTypes prim) {

            if (prim == PrimitiveTypes.INT)
                return intEmitter.emit(node, visitor);

            if (prim == PrimitiveTypes.DOUBLE)
                return doubleEmitter.emit(node, visitor);

            if (prim == PrimitiveTypes.BOOL)
                return boolEmitter.emit(node, visitor);

            if (prim == PrimitiveTypes.STRING)
                return stringEmitter.emit(node, visitor);
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
                .append(" = bitcast i8* ")
                .append(listPtr)
                .append(" to %ArrayList*\n");

        if (elementType instanceof StructType) {

            for (ASTNode element : elements) {

                String elemLLVM = element.accept(visitor);
                llvm.append(elemLLVM);

                String temp = extractTemp(elemLLVM);
                String type = extractType(elemLLVM);

                llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                        .append(listCast)
                        .append(", ")
                        .append(type)
                        .append(" ")
                        .append(temp)
                        .append(")\n");
            }

            llvm.append(";;VAL:")
                    .append(listCast)
                    .append(";;TYPE:%ArrayList*\n");

            return llvm.toString();
        }

        throw new RuntimeException(
                "Unsupported list element type: " + elementType
        );
    }

    private String extractTemp(String code) {

        int idx = code.lastIndexOf(";;VAL:");
        int endIdx = code.indexOf(";;TYPE:", idx);

        if (idx == -1 || endIdx == -1)
            throw new RuntimeException("Invalid LLVM marker format");

        return code.substring(idx + 6, endIdx).trim();
    }

    private String extractType(String code) {

        int idx = code.lastIndexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", idx);

        if (idx == -1)
            throw new RuntimeException("Invalid LLVM marker format");

        if (endIdx == -1)
            endIdx = code.length();

        return code.substring(idx + 7, endIdx).trim();
    }
}