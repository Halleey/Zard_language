package low.prints;

import ast.ASTNode;
import ast.lists.ListGetNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.lists.generics.ListGetEmitter;
import low.module.LLVisitorMain;
public class ListGetPrintHandler implements PrintHandler {
    private final TempManager temps;
    private final ListGetEmitter listGetEmitter;

    public ListGetPrintHandler(TempManager temps, ListGetEmitter listGetEmitter) {
        this.temps = temps;
        this.listGetEmitter = listGetEmitter;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        return node instanceof ListGetNode;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor) {
        ListGetNode getNode = (ListGetNode) node;

        ASTNode listRef = getNode.getListName();
        String listName;
        if (listRef instanceof VariableNode vn) {
            listName = vn.getName();
        } else {
            throw new RuntimeException("ListGetPrintHandler: list reference is not a VariableNode");
        }

        String elemType = visitor.getListElementType(listName);
        String getCode = listGetEmitter.emit(getNode, visitor);
        String valTemp = extractTemp(getCode);

        StringBuilder sb = new StringBuilder();
        appendCodePrefix(sb, getCode);

        // string
        if ("string".equals(elemType)) {
            sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), ")
                    .append("i8* ").append(valTemp).append(")\n");

            // int
        } else if ("int".equals(elemType)) {
            sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), ")
                    .append("i32 ").append(valTemp).append(")\n");

            // double
        } else if ("double".equals(elemType)) {
            sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), ")
                    .append("double ").append(valTemp).append(")\n");

            // boolean
        } else if ("boolean".equals(elemType)) {
            // imprime como 0/1
            sb.append("  %tbool = zext i1 ").append(valTemp).append(" to i32\n");
            sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), ")
                    .append("i32 %tbool)\n");

            // structs
        } else {
            String structName = normalizeStructName(elemType);
            sb.append("  call void @print_").append(structName)
                    .append("(i8* ").append(valTemp).append(")\n");
        }

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

    private String normalizeStructName(String elemType) {
        String s = elemType.trim();
        if (s.startsWith("Struct<") && s.endsWith(">")) {
            s = s.substring("Struct<".length(), s.length() - 1);
        }
        return s.replace('.', '_');
    }
}

