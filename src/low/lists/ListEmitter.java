package low.lists;

import ast.ASTNode;
import ast.lists.ListNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.module.LLVisitorMain;
import java.util.List;

public class ListEmitter {
    private final TempManager temps;
    private final IntListEmitter intEmitter;

    public ListEmitter(TempManager temps) {
        this.temps = temps;
        this.intEmitter = new IntListEmitter(temps);
    }

    public String emit(ListNode node, LLVisitorMain visitor) {
        // Detectar tipo dos elementos
        String elementType = node.getList().getElementType();
        if (elementType == null || elementType.equals("any")) {
            elementType = visitor.getListElementType(node.getList().getElementType());
        }
        if (elementType == null) {
            elementType = "any";
        }

        // Se for List<int>, delega para IntListEmitter
        if ("int".equals(elementType)) {
            return intEmitter.emit(node, visitor);
        }

        // Caso genérico (todos os outros tipos)
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr)
                .append(" = call i8* @arraylist_create(i64 ")
                .append(Math.max(4, n)).append(")\n");

        String listCast = temps.newTemp();
        llvm.append("  ").append(listCast)
                .append(" = bitcast i8* ").append(listPtr).append(" to %ArrayList*\n");

        for (ASTNode element : elements) {
            String elemLLVM = element.accept(visitor);
            llvm.append(elemLLVM);

            String temp = extractTemp(elemLLVM);
            String type = extractType(elemLLVM);

            switch (type) {
                case "double" -> llvm.append("  call void @arraylist_add_double(%ArrayList* ")
                        .append(listCast).append(", double ").append(temp).append(")\n");
                case "%String*" -> {
                    String tmp = element instanceof VariableNode varNode
                            ? "%"+varNode.getName()
                            : temps.newTemp();
                    if (!(element instanceof VariableNode)) {
                        llvm.append("  ").append(tmp)
                                .append(" = call %String* @createString(i8* ").append(temp).append(")\n");
                    }
                    llvm.append("  call void @arraylist_add_String(%ArrayList* ")
                            .append(listCast).append(", %String* ").append(tmp).append(")\n");
                }
                case "i8*" -> llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                        .append(listCast).append(", i8* ").append(temp).append(")\n");
                default -> {
                    if (type.matches("\\[\\d+ x i8\\]\\*")) {
                        String castTmp = temps.newTemp();
                        llvm.append("  ").append(castTmp)
                                .append(" = bitcast ").append(type).append(" ").append(temp)
                                .append(" to i8*\n");
                        llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                                .append(listCast).append(", i8* ").append(castTmp).append(")\n");
                    } else {
                        throw new RuntimeException("Unsupported list element type: " + type);
                    }
                }
            }
        }

        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");
        return llvm.toString();
    }

    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        int endIdx = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, endIdx).trim();
    }

    private String extractType(String code) {
        int idx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", idx);
        return code.substring(idx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
