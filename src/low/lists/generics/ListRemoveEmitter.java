package low.lists.generics;

import ast.lists.ListRemoveNode;
import low.TempManager;
import low.lists.bool.ListBoolRemoveEmitter;
import low.lists.doubles.ListDoubleRemoveEmitter;
import low.lists.ints.ListRemoveIntEmitter;
import low.module.LLVMEmitVisitor;
public class ListRemoveEmitter {
    private final TempManager temps;
    private final ListRemoveIntEmitter intEmitter;
    private final ListDoubleRemoveEmitter doubleRemoveEmitter;
    private final ListBoolRemoveEmitter boolRemoveEmitter;

    public ListRemoveEmitter(TempManager temps) {
        this.temps = temps;
        this.intEmitter = new ListRemoveIntEmitter(temps);
        this.doubleRemoveEmitter = new ListDoubleRemoveEmitter(temps);
        this.boolRemoveEmitter = new ListBoolRemoveEmitter(temps);
    }

    public String emit(ListRemoveNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        String type = extractLastType(listCode);
        String listVal = extractLastVal(listCode);

        if (type.contains("ArrayListInt")) {
            return intEmitter.emit(node, visitor);
        }
        if (type.contains("ArrayListDouble")) {
            return doubleRemoveEmitter.emit(node, visitor);
        }
        if (type.contains("ArrayListBool")) {
            return boolRemoveEmitter.emit(node, visitor);
        }

        // genérico (listas de ponteiros)
        String posCode = node.getIndexNode().accept(visitor);
        llvm.append(listCode);
        llvm.append(posCode);
        String posVal = extractLastVal(posCode);

        String posCast = temps.newTemp();
        llvm.append("  ").append(posCast)
                .append(" = zext i32 ").append(posVal).append(" to i64\n");

        if (type.contains("ArrayList")) {
            llvm.append("  call void @removeItem(%ArrayList* ")
                    .append(listVal).append(", i64 ").append(posCast).append(")\n");
        } else {
            // listVal é i8* -> bitcast para %ArrayList*
            String listCast = temps.newTemp();
            llvm.append("  ").append(listCast)
                    .append(" = bitcast i8* ").append(listVal).append(" to %ArrayList*\n");
            llvm.append("  call void @removeItem(%ArrayList* ")
                    .append(listCast).append(", i64 ").append(posCast).append(")\n");
        }

        return llvm.toString();
    }

    private String extractLastVal(String code) {
        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";
        int t = code.indexOf(";;TYPE:", v);
        return (t == -1) ? "" : code.substring(v + 6, t).trim();
    }

    private String extractLastType(String code) {
        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "";
        int end = code.indexOf("\n", t);
        if (end == -1) end = code.length();
        return code.substring(t + 7, end).trim();
    }
}
