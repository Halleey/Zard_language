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

        // Pega o tipo do elemento da lista com inferência
        String elemType = visitor.inferListElementType(listRef);

        // Gera o IR do acesso (já inclui ;;VAL: e ;;TYPE:)
        String getCode = listGetEmitter.emit(getNode, visitor);
        String valTemp = extractTemp(getCode);

        StringBuilder sb = new StringBuilder();
        appendCodePrefix(sb, getCode);

        if ("string".equals(elemType)) {
            sb.append("  call void @printString(%String* ").append(valTemp).append(")\n");
        }
        else if ("int".equals(elemType)) {
            sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), ")
                    .append("i32 ").append(valTemp).append(")\n");

        } else if ("double".equals(elemType)) {
            sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), ")
                    .append("double ").append(valTemp).append(")\n");

        } else if ("boolean".equals(elemType)) {
            String boolTmp = temps.newTemp();
            sb.append("  ").append(boolTmp).append(" = zext i1 ").append(valTemp).append(" to i32\n");
            sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), ")
                    .append("i32 ").append(boolTmp).append(")\n");

        } else {
            // structs
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

