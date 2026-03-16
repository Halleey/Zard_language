package low.prints;

import ast.ASTNode;
import ast.lists.ListGetNode;
import ast.variables.VariableNode;
import context.statics.symbols.*;
import low.TempManager;
import low.lists.generics.ListGetEmitter;
import low.main.TypeInfos;
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
    public String emit(ASTNode node, LLVisitorMain visitor, boolean newline) {

        ListGetNode getNode = (ListGetNode) node;
        ASTNode listRef = getNode.getListName();

        Type elementType = resolveElementType(listRef, visitor);

        String getCode = listGetEmitter.emit(getNode, visitor);
        String valTemp = extractTemp(getCode);

        StringBuilder sb = new StringBuilder();
        appendCodePrefix(sb, getCode);

        String labelSuffix = newline ? "" : "_noNL";

        if (elementType instanceof PrimitiveTypes p) {

            if (p == PrimitiveTypes.INT) {

                sb.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt")
                        .append(labelSuffix)
                        .append(", i32 0, i32 0), i32 ")
                        .append(valTemp)
                        .append(")\n");
            }

            else if (p == PrimitiveTypes.DOUBLE || p == PrimitiveTypes.FLOAT) {

                sb.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble")
                        .append(labelSuffix)
                        .append(", i32 0, i32 0), double ")
                        .append(valTemp)
                        .append(")\n");
            }

            else if (p == PrimitiveTypes.STRING) {

                sb.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strStr")
                        .append(labelSuffix)
                        .append(", i32 0, i32 0), i8* ")
                        .append(valTemp)
                        .append(")\n");
            }

            else if (p == PrimitiveTypes.BOOL) {

                String boolTmp = temps.newTemp();

                sb.append("  ").append(boolTmp)
                        .append(" = zext i1 ")
                        .append(valTemp)
                        .append(" to i32\n");

                sb.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt")
                        .append(labelSuffix)
                        .append(", i32 0, i32 0), i32 ")
                        .append(boolTmp)
                        .append(")\n");
            }

            else {
                sb.append("  ; unsupported primitive type: ").append(p).append("\n");
            }
        }

        else if (elementType instanceof StructType s) {

            String structName = normalizeStructName(s.name());

            sb.append("  call void @print_")
                    .append(structName)
                    .append("(i8* ")
                    .append(valTemp)
                    .append(")\n");
        }

        else if (elementType instanceof ListType) {

            sb.append("  ; nested list printing not implemented\n");
        }

        else {

            sb.append("  ; unknown element type: ")
                    .append(elementType)
                    .append("\n");
        }

        return sb.toString();
    }

    /**
     * Resolve o tipo do elemento da lista usando TypeInfos
     */
    private Type resolveElementType(ASTNode listRef, LLVisitorMain visitor) {

        if (listRef instanceof VariableNode varNode) {

            TypeInfos info = visitor.getVarType(varNode.getName());

            if (info == null)
                throw new RuntimeException("Variável não registrada: " + varNode.getName());

            if (!(info.getType() instanceof ListType lt))
                throw new RuntimeException("Variável não é lista: " + varNode.getName());

            return lt.elementType();
        }

        Type inferred = visitor.inferListElementType(listRef);

        if (inferred == null)
            throw new RuntimeException("Não foi possível inferir tipo da lista");

        return inferred;
    }

    private void appendCodePrefix(StringBuilder llvm, String code) {

        int marker = code.lastIndexOf(";;VAL:");
        String prefix = (marker == -1) ? code : code.substring(0, marker);

        if (!prefix.isEmpty()) {

            if (!prefix.endsWith("\n"))
                prefix += "\n";

            llvm.append(prefix);
        }
    }

    private String extractTemp(String code) {

        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";

        int t = code.indexOf(";;TYPE:", v);
        if (t == -1) return "";

        return code.substring(v + 6, t).trim();
    }

    private String normalizeStructName(String elemType) {

        String s = elemType.trim();

        if (s.startsWith("Struct<") && s.endsWith(">")) {
            s = s.substring(7, s.length() - 1);
        }

        return s.replace('.', '_');
    }
}