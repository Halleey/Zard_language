package low.lists.bool;

import ast.ASTNode;
import ast.lists.ListAddAllNode;
import low.TempManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMBool;

public class ListBoolAddAllEmitter {

    private final TempManager tempManager;

    public ListBoolAddAllEmitter(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public LLVMValue emit(ListAddAllNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        // Lista alvo
        LLVMValue listVal = node.getTargetListNode().accept(visitor);
        llvm.append(listVal.getCode());
        String listTmp = listVal.getName();

        int n = node.getArgs().size();
        if (n == 0) {
            return listVal; // retorna a lista original se não houver elementos
        }

        // Alocando array temporário de i8 (para bools)
        String tmpArray = tempManager.newTemp();
        llvm.append("  ").append(tmpArray)
                .append(" = alloca [").append(n).append(" x i8], align 1\n");

        String basePtr = tempManager.newTemp();
        llvm.append("  ").append(basePtr)
                .append(" = getelementptr inbounds [").append(n).append(" x i8], [")
                .append(n).append(" x i8]* ").append(tmpArray).append(", i32 0, i32 0)\n");

        // Preenchendo o array com zext de i1 para i8
        for (int i = 0; i < n; i++) {
            LLVMValue val = node.getArgs().get(i).accept(visitor);
            llvm.append(val.getCode());
            String valTmp = val.getName();

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

        // Chamando função do runtime
        llvm.append("  call void @arraylist_addAll_bool(%struct.ArrayListBool* ")
                .append(listTmp).append(", i8* ").append(basePtr)
                .append(", i64 ").append(n).append(")\n");

        return new LLVMValue(
                new LLVMArrayList(new LLVMBool()),
                listTmp,
                llvm.toString()
        );
    }
}