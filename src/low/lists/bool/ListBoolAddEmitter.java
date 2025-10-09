package low.lists.bool;

import ast.lists.ListAddNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListBoolAddEmitter {
    private final TempManager tempManager;

    public ListBoolAddEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListAddNode node, LLVMEmitVisitor visitor){
        StringBuilder llvm = new StringBuilder();
        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);
        String listTemp = extractTemp(listCode);
        String valCode = node.getValuesNode().accept(visitor);
        llvm.append(valCode);
        String valTmp = extractTemp(valCode);


        llvm.append("  call void @arraylist_add_bool(%struct.ArrayListBool* ").append(listTemp)
                .append(", i1").append(valTmp).append(")\n");

        llvm.append(";;VAL:").append(listTemp).append(";;TYPE:struct.ArrayListBool*\n");
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
