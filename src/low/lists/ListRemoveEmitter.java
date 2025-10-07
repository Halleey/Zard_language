package low.lists;

import ast.lists.ListRemoveNode;
import low.TempManager;
import low.lists.ints.ListRemoveIntEmitter;
import low.module.LLVMEmitVisitor;
public class ListRemoveEmitter {
    private final TempManager temps;
    private final ListRemoveIntEmitter intEmitter; // emitter específico para inteiros

    public ListRemoveEmitter(TempManager temps) {
        this.temps = temps;
        this.intEmitter = new ListRemoveIntEmitter(temps);
    }

    public String emit(ListRemoveNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        String type = extractType(listCode);
        String listVal = extractValue(listCode);

        if (type.contains("ArrayListInt")) {
            return intEmitter.emit(node, visitor);
        }

        // Código e valor do índice
        String posCode = node.getIndexNode().accept(visitor);
        llvm.append(listCode);
        llvm.append(posCode);
        String posVal = extractValue(posCode);

        // Cast do índice para i64
        String posCast = temps.newTemp();
        llvm.append("  ").append(posCast)
                .append(" = sext i32 ").append(posVal).append(" to i64\n");

        // Cast de i8* para %ArrayList* (genérico)
        String listCast = temps.newTemp();
        llvm.append("  ").append(listCast)
                .append(" = bitcast i8* ").append(listVal)
                .append(" to %ArrayList*\n");

        // Chamada ao runtime removeItem(ArrayList*, size_t)
        llvm.append("  call void @removeItem(%ArrayList* ")
                .append(listCast).append(", i64 ").append(posCast).append(")\n");

        return llvm.toString();
    }

    private String extractValue(String code) {
        for (String line : code.split("\n")) {
            if (line.contains(";;VAL:")) {
                String val = line.split(";;VAL:")[1].trim();
                if (val.contains(";;TYPE")) {
                    val = val.split(";;TYPE")[0].trim();
                }
                return val;
            }
        }
        return null;
    }

    private String extractType(String code) {
        int typeIdx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", typeIdx);
        return code.substring(typeIdx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
