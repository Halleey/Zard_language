package low.lists.bool;

import ast.ASTNode;
import ast.lists.ListAddAllNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListBoolAddAllEmitter {
    private final TempManager tempManager;


    public ListBoolAddAllEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public String emit(ListAddAllNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();
        ASTNode targetList = node.getTargetListNode();
        String listCode = targetList.accept(visitor);
        llvm.append(listCode);
        String listTmp = extractTemp(listCode);

        int n = node.getArgs().size();
        if(n == 0) return llvm.toString();;

        String tmpArray = tempManager.newTemp();
        llvm.append("  ").append(tmpArray)
                .append(" = alloca i1, i64 ").append(n).append("\n");

        for (int i = 0; i < n; i++) {
            ASTNode valueNode = node.getArgs().get(i);
            String valCode = valueNode.accept(visitor);
            llvm.append(valCode);
            String valTmp = extractTemp(valCode);

            String gepTmp = tempManager.newTemp();
            llvm.append("  ").append(gepTmp)
                    .append(" = getelementptr inbounds i1, i1* ")
                    .append(tmpArray).append(", i64 ").append(i).append("\n");

            llvm.append("  store i1 ").append(valTmp)
                    .append(", i1* ").append(gepTmp).append("\n");
        }

        llvm.append("  call void @arraylist_addAll_bool(%struct.ArrayListBool* ")
                .append(listTmp)
                .append(", i1* ").append(tmpArray)
                .append(", i64 ").append(n).append(")\n");

        llvm.append(";;VAL:").append(listTmp)
                .append(";;TYPE:%struct.ArrayListBool*\n");

        return llvm.toString();
    }



    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        int endIdx = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, endIdx).trim();
    }

    private String extractType(String code) {
        int idx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", idx);
        return code.substring(idx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
