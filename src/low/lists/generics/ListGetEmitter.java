package low.lists.generics;

import ast.lists.ListGetNode;
import low.TempManager;
import low.lists.doubles.ListGetDoubleEmitter;
import low.lists.ints.ListGetIntEmitter;
import low.module.LLVMEmitVisitor;



public class ListGetEmitter {
    private final TempManager temps;
    private final ListGetIntEmitter intGetEmitter;
    private final ListGetDoubleEmitter doubleEmitter;
    public ListGetEmitter(TempManager temps) {
        this.temps = temps;
        this.intGetEmitter = new ListGetIntEmitter(temps);
        this.doubleEmitter = new ListGetDoubleEmitter(temps);
    }

    public String emit(ListGetNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListName().accept(visitor);

        String listType = extractType(listCode);
        String listTemp = extractTemp(listCode);

        if (listType.contains("ArrayListInt")) {
            return intGetEmitter.emit(node, visitor);
        }
        if (listType.contains("ArrayListDouble")) {
            return doubleEmitter.emit(node, visitor);
        }
        appendCodePrefix(llvm, listCode);

        String arrayPtr = temps.newTemp();
        llvm.append("  ").append(arrayPtr)
                .append(" = bitcast i8* ").append(listTemp)
                .append(" to %ArrayList*\n");

        String idxCode = node.getIndexNode().accept(visitor);
        String idxTemp = extractTemp(idxCode);
        appendCodePrefix(llvm, idxCode);

        String idx64 = temps.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ").append(idxTemp)
                .append(" to i64\n");

        String valTemp = temps.newTemp();
        llvm.append("  ").append(valTemp)
                .append(" = call i8* @getItem(%ArrayList* ")
                .append(arrayPtr).append(", i64 ").append(idx64).append(")\n");

        llvm.append(";;VAL:").append(valTemp).append("\n");
        llvm.append(";;TYPE:i8*\n");

        return llvm.toString();
    }


    private void appendCodePrefix(StringBuilder llvm, String code) {
        int marker = code.lastIndexOf(";;VAL:");
        String prefix = (marker == -1) ? code : code.substring(0, marker);
        if (!prefix.isEmpty()) {
            if (!prefix.endsWith("\n")) prefix += "\n";
            llvm.append(prefix);
        }
    }

    private String extractTemp(String code) {
        int v = code.indexOf(";;VAL:");
        int t = code.indexOf(";;TYPE:", v);
        return (v == -1 || t == -1) ? "" : code.substring(v + 6, t).trim();
    }

    private String extractType(String code) {
        int t = code.indexOf(";;TYPE:");
        if (t == -1) return "";
        int end = code.indexOf("\n", t);
        return code.substring(t + 7, (end == -1 ? code.length() : end)).trim();
    }
}
