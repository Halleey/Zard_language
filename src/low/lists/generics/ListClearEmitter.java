package low.lists.generics;

import ast.lists.ListClearNode;
import low.TempManager;
import low.lists.doubles.ListDoubleClearEmitter;
import low.lists.ints.ListIntClearEmitter;
import low.module.LLVMEmitVisitor;


public class ListClearEmitter {
    private final TempManager temps;
    private final ListIntClearEmitter listIntClearEmitter;
    private final ListDoubleClearEmitter doubleClearEmitter;
    public ListClearEmitter(TempManager temps) {
        this.temps = temps;
        this.listIntClearEmitter = new ListIntClearEmitter(temps);
        this.doubleClearEmitter = new ListDoubleClearEmitter(temps);
    }

    public String emit(ListClearNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);

        String listTmp = extractTemp(listCode);
        String valType = extractType(listCode);


        System.out.println("--------" + valType);
        // Delegar para o emitter específico se for lista de int
        if (valType.contains("ArrayListInt")) {
            return listIntClearEmitter.emit(node, visitor);
        }
        if (valType.contains("ArrayListDouble")) {
            return doubleClearEmitter.emit(node, visitor);
        }

        // Caso genérico - outras listas (ex: string, String, etc.)
        String listCastTmp = temps.newTemp();
        llvm.append("  ").append(listCastTmp)
                .append(" = bitcast ").append(valType).append(" ").append(listTmp)
                .append(" to %ArrayList*\n");

        llvm.append("  call void @clearList(%ArrayList* ").append(listCastTmp).append(")\n");

        llvm.append(";;VAL:").append(listCastTmp).append(";;TYPE:%ArrayList*\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int typeIdx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", typeIdx);
        return code.substring(typeIdx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
