package low.lists.generics;

import ast.lists.ListGetNode;
import low.TempManager;
import low.lists.bool.ListBoolGetEmitter;
import low.lists.doubles.ListGetDoubleEmitter;
import low.lists.ints.ListGetIntEmitter;
import low.module.LLVisitorMain;

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

    public String emit(ListGetNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();


        String listCode = node.getListName().accept(visitor);
        appendCodePrefix(llvm, listCode);

        String listType = extractType(listCode);
        String listTemp = extractTemp(listCode);

        // Se já for uma das listas primitivas, delega pros emitters específicos
        if (listType.contains("ArrayListInt")) {
            return intGetEmitter.emit(node, visitor);
        }
        if (listType.contains("ArrayListDouble")) {
            return doubleEmitter.emit(node, visitor);
        }
        if (listType.contains("ArrayListBool")) {
            return boolGetEmitter.emit(node, visitor);
        }

        // ===== Caminho genérico (listas de ponteiros / structs / strings) =====

        String idxCode = node.getIndexNode().accept(visitor);
        appendCodePrefix(llvm, idxCode);
        String idxTemp = extractTemp(idxCode);

        String idx64 = temps.newTemp();
        llvm.append("  ").append(idx64)
                .append(" = zext i32 ").append(idxTemp).append(" to i64\n");

        String rawTemp = temps.newTemp();
        llvm.append("  ").append(rawTemp)
                .append(" = call i8* @arraylist_get_ptr(%ArrayList* ")
                .append(listTemp).append(", i64 ").append(idx64).append(")\n");

        String elemType = visitor.inferListElementType(node.getListName());
        String castTemp = temps.newTemp();

        if ("string".equals(elemType)) {

            llvm.append(";;VAL:").append(rawTemp).append("\n");
            llvm.append(";;TYPE:i8*\n");
        }
        else if ("int".equals(elemType)) {
            String intPtr = temps.newTemp();
            llvm.append("  ").append(intPtr)
                    .append(" = bitcast i8* ").append(rawTemp).append(" to i32*\n");
            String intVal = temps.newTemp();
            llvm.append("  ").append(intVal)
                    .append(" = load i32, i32* ").append(intPtr).append("\n");
            llvm.append(";;VAL:").append(intVal).append("\n");
            llvm.append(";;TYPE:i32\n");
        } else if ("double".equals(elemType)) {
            String dblPtr = temps.newTemp();
            llvm.append("  ").append(dblPtr)
                    .append(" = bitcast i8* ").append(rawTemp).append(" to double*\n");
            String dblVal = temps.newTemp();
            llvm.append("  ").append(dblVal)
                    .append(" = load double, double* ").append(dblPtr).append("\n");
            llvm.append(";;VAL:").append(dblVal).append("\n");
            llvm.append(";;TYPE:double\n");
        } else if ("boolean".equals(elemType)) {
            String boolPtr = temps.newTemp();
            llvm.append("  ").append(boolPtr)
                    .append(" = bitcast i8* ").append(rawTemp).append(" to i1*\n");
            String boolVal = temps.newTemp();
            llvm.append("  ").append(boolVal)
                    .append(" = load i1, i1* ").append(boolPtr).append("\n");
            llvm.append(";;VAL:").append(boolVal).append("\n");
            llvm.append(";;TYPE:i1\n");
        } else {
            // structs
            String structName = normalizeStructName(elemType);
            llvm.append("  ").append(castTemp)
                    .append(" = bitcast i8* ").append(rawTemp)
                    .append(" to %").append(structName).append("*\n");
            llvm.append(";;VAL:").append(castTemp).append("\n");
            llvm.append(";;TYPE:%").append(structName).append("*\n");
        }

        return llvm.toString();
    }

    private String normalizeStructName(String elemType) {
        String s = elemType.trim();
        if (s.startsWith("Struct<") && s.endsWith(">")) {
            s = s.substring("Struct<".length(), s.length() - 1);
        }
        return s.replace('.', '_');
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
        int v = code.lastIndexOf(";;VAL:");
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
