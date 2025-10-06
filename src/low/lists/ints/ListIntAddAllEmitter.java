package low.lists.ints;

import ast.ASTNode;
import ast.lists.ListAddAllNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;


public class ListIntAddAllEmitter {
    private final TempManager temps;

    public ListIntAddAllEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListAddAllNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        ASTNode targetListNode = node.getTargetListNode();
        String listCode = targetListNode.accept(visitor);
        llvm.append(listCode);
        String listTmp = extractTemp(listCode);

        int n = node.getArgs().size();
        if (n == 0) return llvm.toString();

        String tmpArray = temps.newTemp();
        llvm.append("  ").append(tmpArray)
                .append(" = alloca i32, i64 ").append(n).append("\n");

        for (int i = 0; i < n; i++) {
            ASTNode valueNode = node.getArgs().get(i);
            String valCode = valueNode.accept(visitor);
            llvm.append(valCode);
            String valTmp = extractTemp(valCode);

            String gepTmp = temps.newTemp();
            llvm.append("  ").append(gepTmp)
                    .append(" = getelementptr inbounds i32, i32* ")
                    .append(tmpArray).append(", i64 ").append(i).append("\n");

            llvm.append("  store i32 ").append(valTmp)
                    .append(", i32* ").append(gepTmp).append("\n");
        }

        llvm.append("  call void @arraylist_addAll_int(%struct.ArrayListInt* ")
                .append(listTmp)
                .append(", i32* ").append(tmpArray)
                .append(", i64 ").append(n).append(")\n");

        llvm.append(";;VAL:").append(listTmp)
                .append(";;TYPE:%struct.ArrayListInt*\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        if (lastValIdx == -1 || typeIdx == -1)
            throw new RuntimeException("Failed to extract temp in ListIntAddAllEmitter:\n" + code);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
