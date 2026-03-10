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

        if (listTemp.isEmpty()) {
            throw new RuntimeException("ListGetEmitter: could not extract list temp:\n" + listCode);
        }

        // Delegação para listas primitivas especializadas
        if (listType.contains("ArrayListInt")) {
            return intGetEmitter.emit(node, visitor);
        }

        if (listType.contains("ArrayListDouble")) {
            return doubleEmitter.emit(node, visitor);
        }

        if (listType.contains("ArrayListBool")) {
            return boolGetEmitter.emit(node, visitor);
        }

        String idxCode = node.getIndexNode().accept(visitor);
        appendCodePrefix(llvm, idxCode);

        String idxTemp = extractTemp(idxCode);

        if (idxTemp.isEmpty()) {
            throw new RuntimeException("ListGetEmitter: could not extract index temp:\n" + idxCode);
        }

        String idx64 = temps.newTemp();
        llvm.append("  ")
                .append(idx64)
                .append(" = zext i32 ")
                .append(idxTemp)
                .append(" to i64\n");

        String rawTemp = temps.newTemp();
        llvm.append("  ")
                .append(rawTemp)
                .append(" = call i8* @arraylist_get_ptr(%ArrayList* ")
                .append(listTemp)
                .append(", i64 ")
                .append(idx64)
                .append(")\n");

        Type elemType = node.getElementType();

        if (elemType == null) {
            throw new RuntimeException(
                    "ListGetEmitter: element type missing in node"
            );
        }

        if (elemType instanceof PrimitiveTypes prim) {

            if (prim == PrimitiveTypes.STRING) {

                llvm.append(";;VAL:").append(rawTemp).append("\n");
                llvm.append(";;TYPE:%String*\n");

            } else if (prim == PrimitiveTypes.INT) {

                String intPtr = temps.newTemp();
                llvm.append("  ").append(intPtr)
                        .append(" = bitcast i8* ").append(rawTemp).append(" to i32*\n");

                String intVal = temps.newTemp();
                llvm.append("  ").append(intVal)
                        .append(" = load i32, i32* ").append(intPtr).append("\n");

                llvm.append(";;VAL:").append(intVal).append("\n");
                llvm.append(";;TYPE:i32\n");

            } else if (prim == PrimitiveTypes.DOUBLE) {

                String dblPtr = temps.newTemp();
                llvm.append("  ").append(dblPtr)
                        .append(" = bitcast i8* ").append(rawTemp).append(" to double*\n");

                String dblVal = temps.newTemp();
                llvm.append("  ").append(dblVal)
                        .append(" = load double, double* ").append(dblPtr).append("\n");

                llvm.append(";;VAL:").append(dblVal).append("\n");
                llvm.append(";;TYPE:double\n");

            } else if (prim == PrimitiveTypes.BOOL) {

                String boolPtr = temps.newTemp();
                llvm.append("  ").append(boolPtr)
                        .append(" = bitcast i8* ").append(rawTemp).append(" to i1*\n");

                String boolVal = temps.newTemp();
                llvm.append("  ").append(boolVal)
                        .append(" = load i1, i1* ").append(boolPtr).append("\n");

                llvm.append(";;VAL:").append(boolVal).append("\n");
                llvm.append(";;TYPE:i1\n");

            } else {
                throw new RuntimeException("Unsupported primitive list type: " + prim);
            }
        }

        else if (elemType instanceof StructType struct) {

            String castTemp = temps.newTemp();

            llvm.append("  ")
                    .append(castTemp)
                    .append(" = bitcast i8* ")
                    .append(rawTemp)
                    .append(" to %")
                    .append(struct.name())
                    .append("*\n");

            llvm.append(";;VAL:").append(castTemp).append("\n");
            llvm.append(";;TYPE:%").append(struct.name()).append("*\n");
        }

        else {
            throw new RuntimeException("Unsupported list element type: " + elemType);
        }

        return llvm.toString();
    }

    private void appendCodePrefix(StringBuilder llvm, String code) {

        int marker = code.lastIndexOf(";;VAL:");
        String prefix = (marker == -1) ? code : code.substring(0, marker);

        if (!prefix.isEmpty()) {
            llvm.append(prefix);
            if (!prefix.endsWith("\n")) {
                llvm.append("\n");
            }
        }
    }

    private String extractTemp(String code) {

        int v = code.lastIndexOf(";;VAL:");
        int t = code.indexOf(";;TYPE:", v);

        if (v == -1 || t == -1) {
            throw new RuntimeException("Failed to extract temp from:\n" + code);
        }

        return code.substring(v + 6, t).trim();
    }

    private String extractType(String code) {

        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "";

        int end = code.indexOf("\n", t);
        if (end == -1) end = code.length();

        return code.substring(t + 7, end).trim();
    }
}