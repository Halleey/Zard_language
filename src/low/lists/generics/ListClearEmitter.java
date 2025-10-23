package low.lists.generics;

import ast.lists.ListClearNode;
import low.TempManager;
import low.lists.bool.ListBoolClearEmitter;
import low.lists.doubles.ListDoubleClearEmitter;
import low.lists.ints.ListIntClearEmitter;
import low.module.LLVMEmitVisitor;


public class ListClearEmitter {
    private final TempManager temps;
    private final ListIntClearEmitter listIntClearEmitter;
    private final ListDoubleClearEmitter doubleClearEmitter;
    private final ListBoolClearEmitter boolClearEmitter;


    public ListClearEmitter(TempManager temps) {
        this.temps = temps;
        this.listIntClearEmitter = new ListIntClearEmitter(temps);
        this.doubleClearEmitter = new ListDoubleClearEmitter(temps);
        this.boolClearEmitter = new ListBoolClearEmitter(temps);
    }

    public String emit(ListClearNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();
        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);

        String listTmp = extractTemp(listCode);
        String valType = extractType(listCode);

        // 🔍 Debugs
        System.out.println("==== DEBUG ListClearEmitter ====");
        System.out.println("LLVM recebido:\n" + listCode);
        System.out.println("Temp extraído: " + listTmp);
        System.out.println("Tipo extraído: " + valType);
        System.out.println("===============================");

        if (valType.contains("ArrayListInt")) {
            return listIntClearEmitter.emit(node, visitor);
        }
        if (valType.contains("ArrayListDouble")) {
            return doubleClearEmitter.emit(node, visitor);
        }
        if (valType.contains("ArrayListBool")) {
            return boolClearEmitter.emit(node, visitor);
        }

        String listCastTmp = temps.newTemp();
        llvm.append("  ").append(listCastTmp)
                .append(" = bitcast ").append(valType).append(" ").append(listTmp)
                .append(" to %ArrayList*\n");
        llvm.append(";;VAL:").append(listCastTmp).append(";;TYPE:%ArrayList*\n");

        return llvm.toString();
    }


    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", lastTypeIdx);
        return code.substring(lastTypeIdx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }

}
