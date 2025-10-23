package low.prints;

import ast.ASTNode;
import ast.lists.ListSizeNode;
import low.TempManager;
import low.lists.generics.ListSizeEmitter;
import low.module.LLVisitorMain;

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
    public String emit(ASTNode node, LLVisitorMain visitor) {
        ListSizeNode sizeNode = (ListSizeNode) node;

        String sizeCode = listSizeEmitter.emit(sizeNode, visitor);
        String valTemp = extractTemp(sizeCode);

        StringBuilder sb = new StringBuilder();
        appendCodePrefix(sb, sizeCode);

        sb.append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), ")
                .append("i32 ").append(valTemp).append(")\n");

        return sb.toString();
    }

    private void appendCodePrefix(StringBuilder llvm, String code) {
        int marker = code.lastIndexOf(";;VAL:");
        String prefix = (marker == -1) ? code : code.substring(0, marker);
        if (!prefix.isEmpty()) {
            if (!prefix.endsWith("\n")) prefix += "\n";
            llvm.append(prefix);
        }
    }

    private String extractTemp(String code) {
        int v = code.indexOf(";;VAL:");
        int t = code.indexOf(";;TYPE:", v);
        return (v == -1 || t == -1) ? "" : code.substring(v + 6, t).trim();
    }
}
