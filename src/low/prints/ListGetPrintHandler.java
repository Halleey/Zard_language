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

    public String emit(ASTNode node, LLVisitorMain visitor, boolean newline) {
        ListGetNode getNode = (ListGetNode) node;
        ASTNode listRef = getNode.getListName();

        String elemType = visitor.inferListElementType(listRef);

        String getCode = listGetEmitter.emit(getNode, visitor);
        String valTemp = extractTemp(getCode);

        StringBuilder sb = new StringBuilder();
        appendCodePrefix(sb, getCode);

        String labelSuffix = newline ? "" : "_noNL";

        switch (elemType) {
            case "string" -> sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strStr").append(labelSuffix)
                    .append(", i32 0, i32 0), i8* ").append(valTemp).append(")\n");
            case "int" -> sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt").append(labelSuffix)
                    .append(", i32 0, i32 0), i32 ").append(valTemp).append(")\n");
            case "double" -> sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble").append(labelSuffix)
                    .append(", i32 0, i32 0), double ").append(valTemp).append(")\n");
            case "boolean" -> {
                String boolTmp = temps.newTemp();
                sb.append("  ").append(boolTmp).append(" = zext i1 ").append(valTemp).append(" to i32\n");
                sb.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt").append(labelSuffix)
                        .append(", i32 0, i32 0), i32 ").append(boolTmp).append(")\n");
            }
            default -> { // structs
                String structName = normalizeStructName(elemType);
                sb.append("  call void @print_").append(structName)
                        .append("(i8* ").append(valTemp).append(")\n");
            }
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
        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";
        int t = code.indexOf(";;TYPE:", v);
        return (t == -1) ? "" : code.substring(v + 6, t).trim();
    }

    private String normalizeStructName(String elemType) {
        String s = elemType.trim();
        if (s.startsWith("Struct<") && s.endsWith(">")) {
            s = s.substring("Struct<".length(), s.length() - 1);
        }
        return s.replace('.', '_');
    }
}
