package low.lists.generics;

import ast.lists.ListGetNode;
import low.TempManager;
import low.lists.bool.ListBoolGetEmitter;
import low.lists.doubles.ListGetDoubleEmitter;
import low.lists.ints.ListGetIntEmitter;
import low.module.LLVMEmitVisitor;
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

    public String emit(ListGetNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        // gera o código da lista
        String listCode = node.getListName().accept(visitor);
        appendCodePrefix(llvm, listCode);

        String listType = extractType(listCode);
        String listTemp = extractTemp(listCode);

        // casos especializados
        if (listType.contains("ArrayListInt")) {
            return intGetEmitter.emit(node, visitor);
        }
        if (listType.contains("ArrayListDouble")) {
            return doubleEmitter.emit(node, visitor);
        }
        if (listType.contains("ArrayListBool")) {
            return boolGetEmitter.emit(node, visitor);
        }

        // --- fix para struct ---
        String arrayPtr;
        if (listType.startsWith("%") && listType.contains("Set")) {
            String tmpGep = temps.newTemp();
            llvm.append("  ").append(tmpGep)
                    .append(" = getelementptr inbounds ").append(listType)
                    .append(", ").append(listType).append("* ").append(listTemp)
                    .append(", i32 0, i32 0\n");

            String tmpLoad = temps.newTemp();
            llvm.append("  ").append(tmpLoad)
                    .append(" = load %ArrayList*, %ArrayList** ").append(tmpGep).append("\n");

            arrayPtr = tmpLoad;
        } else {
            arrayPtr = listTemp;
        }

        // índice
        String idxCode = node.getIndexNode().accept(visitor);
        appendCodePrefix(llvm, idxCode);
        String idxTemp = extractTemp(idxCode);

        String idx64 = temps.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ").append(idxTemp).append(" to i64\n");

        // chamada runtime
        String rawTemp = temps.newTemp();
        llvm.append("  ").append(rawTemp)
                .append(" = call i8* @arraylist_get_ptr(%ArrayList* ")
                .append(arrayPtr).append(", i64 ").append(idx64).append(")\n");

        // --- fix para string ---
        String castTemp = temps.newTemp();
        llvm.append("  ").append(castTemp)
                .append(" = bitcast i8* ").append(rawTemp).append(" to %String*\n");

        // markers finais
        llvm.append(";;VAL:").append(castTemp).append("\n");
        llvm.append(";;TYPE:%String*\n");

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
        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "";
        int end = code.indexOf("\n", t);
        return code.substring(t + 7, (end == -1 ? code.length() : end)).trim();
    }
}
