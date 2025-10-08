package low.lists.doubles;

import ast.ASTNode;
import ast.lists.ListAddAllNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;

public class ListAddAllDoubleEmitter {
    private final TempManager temps;

    public ListAddAllDoubleEmitter(TempManager temps) {
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
                .append(" = alloca double, i64 ").append(n).append("\n");

        for (int i = 0; i < n; i++) {
            ASTNode valueNode = node.getArgs().get(i);
            String valCode = valueNode.accept(visitor);
            llvm.append(valCode);
            String valTmp = extractTemp(valCode);

            String gepTmp = temps.newTemp();
            llvm.append("  ").append(gepTmp)
                    .append(" = getelementptr inbounds double, double* ")
                    .append(tmpArray).append(", i64 ").append(i).append("\n");

            llvm.append("  store double ").append(valTmp)
                    .append(", double* ").append(gepTmp).append("\n");
        }

        llvm.append("  call void @arraylist_addAll_double(%struct.ArrayListDouble* ")
                .append(listTmp)
                .append(", double* ").append(tmpArray)
                .append(", i64 ").append(n).append(")\n");

        llvm.append(";;VAL:").append(listTmp)
                .append(";;TYPE:%struct.ArrayListInt*\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        if (lastValIdx == -1 || typeIdx == -1)
            throw new RuntimeException("Failed to extract temp in ListDoubleAddAllEmitter:\n" + code);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}