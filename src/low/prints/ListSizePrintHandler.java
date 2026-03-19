package low.prints;

import ast.ASTNode;
import ast.lists.ListSizeNode;
import low.TempManager;
import low.lists.generics.ListSizeEmitter;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMInt;

public class ListSizePrintHandler implements PrintHandler {

    private final TempManager temps;
    private final ListSizeEmitter listSizeEmitter;

    public ListSizePrintHandler(TempManager temps, ListSizeEmitter listSizeEmitter) {
        this.temps = temps;
        this.listSizeEmitter = listSizeEmitter;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        return node instanceof ListSizeNode;
    }

    @Override
    public LLVMValue emit(ASTNode node, LLVisitorMain visitor, boolean newline) {

        ListSizeNode sizeNode = (ListSizeNode) node;

        LLVMValue val = listSizeEmitter.emit(sizeNode, visitor);

        StringBuilder llvm = new StringBuilder();
        llvm.append(val.getCode());

        String labelSuffix = newline ? "" : "_noNL";

        llvm.append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt")
                .append(labelSuffix)
                .append(", i32 0, i32 0), i32 ")
                .append(val.getName())
                .append(")\n");

        return new LLVMValue(new LLVMInt(), val.getName(), llvm.toString());
    }
}