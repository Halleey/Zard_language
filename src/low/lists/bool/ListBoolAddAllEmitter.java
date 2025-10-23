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
            if (n == 0) return llvm.toString();

            String tmpArray = tempManager.newTemp();
            llvm.append("  ").append(tmpArray)
                    .append(" = alloca [").append(n).append(" x i8], align 1\n");

            String basePtr = tempManager.newTemp();
            llvm.append("  ").append(basePtr)
                    .append(" = getelementptr inbounds [").append(n).append(" x i8], [")
                    .append(n).append(" x i8]* ").append(tmpArray).append(", i32 0, i32 0\n");

            for (int i = 0; i < n; i++) {
                ASTNode valueNode = node.getArgs().get(i);
                String valCode = valueNode.accept(visitor);
                llvm.append(valCode);
                String valTmp = extractTemp(valCode);

                String zextTmp = tempManager.newTemp();
                llvm.append("  ").append(zextTmp)
                        .append(" = zext i1 ").append(valTmp).append(" to i8\n");

                String gepTmp = tempManager.newTemp();
                llvm.append("  ").append(gepTmp)
                        .append(" = getelementptr inbounds i8, i8* ")
                        .append(basePtr).append(", i64 ").append(i).append("\n");

                llvm.append("  store i8 ").append(zextTmp)
                        .append(", i8* ").append(gepTmp).append("\n");
            }

            llvm.append("  call void @arraylist_addAll_bool(%struct.ArrayListBool* ")
                    .append(listTmp).append(", i8* ").append(basePtr)
                    .append(", i64 ").append(n).append(")\n");

            llvm.append(";;VAL:").append(listTmp)
                    .append(";;TYPE:%struct.ArrayListBool*\n");

            return llvm.toString();
        }
        private String extractTemp(String code) {
            int lastValIdx = code.lastIndexOf(";;VAL:");
            int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
            if (lastValIdx == -1 || typeIdx == -1)
                throw new RuntimeException("Failed to extract temp in ListBoolAddAllEmitter:\n" + code);
            return code.substring(lastValIdx + 6, typeIdx).trim();
        }

}
