package low.lists.doubles;

import ast.lists.ListGetNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
public class ListGetDoubleEmitter {
    private final TempManager temps;

    public ListGetDoubleEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListGetNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListName().accept(visitor);
        String listTemp = extractTemp(listCode);
        appendCodePrefix(llvm, listCode);

        String idxCode = node.getIndexNode().accept(visitor);
        String idxTemp = extractTemp(idxCode);
        String idxType = extractType(idxCode);
        appendCodePrefix(llvm, idxCode);

        String idx64 = temps.newTemp();
        if ("i32".equals(idxType)) {
            llvm.append("  ").append(idx64)
                    .append(" = zext i32 ").append(idxTemp)
                    .append(" to i64\n");
        } else if ("i64".equals(idxType)) {
            idx64 = idxTemp; // já é i64
        } else if ("double".equals(idxType)) {
            llvm.append("  ").append(idx64)
                    .append(" = fptosi double ").append(idxTemp)
                    .append(" to i64\n");
        } else {
            llvm.append("; [WARN] Índice de tipo inesperado: ").append(idxType).append("\n");
            llvm.append("  ").append(idx64)
                    .append(" = zext i32 ").append(idxTemp)
                    .append(" to i64\n");
        }

        String outPtr = temps.newTemp();
        llvm.append("  ").append(outPtr)
                .append(" = alloca double\n");

        String success = temps.newTemp();
        llvm.append("  ").append(success)
                .append(" = call double @arraylist_get_double(%struct.ArrayListDouble* ")
                .append(listTemp).append(", i64 ").append(idx64)
                .append(", double* ").append(outPtr).append(")\n");

        String value = temps.newTemp();
        llvm.append("  ").append(value)
                .append(" = load double, double* ").append(outPtr).append("\n");

        llvm.append(";;VAL:").append(value).append("\n");
        llvm.append(";;TYPE:double\n");

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
        if (v == -1 || t == -1) return "";
        return code.substring(v + 6, t).trim();
    }

    private String extractType(String code) {
        int t = code.indexOf(";;TYPE:");
        if (t == -1) return "";
        int end = code.indexOf("\n", t);
        if (end == -1) end = code.length();
        return code.substring(t + 7, end).trim();
    }
}
