package low.lists.generics;

import ast.ASTNode;
import ast.lists.ListNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.lists.bool.ListBoolEmitter;
import low.lists.doubles.ListDoubleEmitter;
import low.lists.ints.IntListEmitter;
import low.module.LLVisitorMain;
import java.util.List;
public class ListEmitter {
    private final TempManager temps;
    private final IntListEmitter intEmitter;
    private final ListDoubleEmitter doubleEmitter;
    private final ListBoolEmitter boolEmitter;

    public ListEmitter(TempManager temps) {
        this.temps = temps;
        this.intEmitter = new IntListEmitter(temps);
        this.doubleEmitter = new ListDoubleEmitter(temps);
        this.boolEmitter = new ListBoolEmitter(temps);
    }

    public String emit(ListNode node, LLVisitorMain visitor) {
        // Detectar tipo dos elementos
        String elementType = node.getList().getElementType();
        if (elementType == null || elementType.equals("any")) {
            elementType = visitor.getListElementType(node.getList().getElementType());
        }

        if ("int".equals(elementType)) {
            return intEmitter.emit(node, visitor);
        }
        if ("double".equals(elementType)) {
            return doubleEmitter.emit(node, visitor);
        }
        if ("boolean".equals(elementType)) {
            return boolEmitter.emit(node, visitor);
        }

        // Caso genérico (strings e outros)
        StringBuilder llvm = new StringBuilder();
        var elements = node.getList().getElements();
        int n = elements.size();

        // Cria lista genérica
        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr)
                .append(" = call i8* @arraylist_create(i64 ")
                .append(Math.max(4, n)).append(")\n");

        String listCast = temps.newTemp();
        llvm.append("  ").append(listCast)
                .append(" = bitcast i8* ").append(listPtr)
                .append(" to %ArrayList*\n");

        for (ASTNode element : elements) {
            String elemLLVM = element.accept(visitor);
            llvm.append(elemLLVM);

            String temp = extractTemp(elemLLVM);
            String type = extractType(elemLLVM);

            String strTmp;

            if (type.equals("%String*")) {
                // já é um objeto String
                strTmp = temp;
            } else if (type.equals("i8*")) {
                // ponteiro de char -> cria %String*
                strTmp = temps.newTemp();
                llvm.append("  ").append(strTmp)
                        .append(" = call %String* @createString(i8* ")
                        .append(temp).append(")\n");
            } else if (type.matches("\\[\\d+ x i8\\]\\*")) {
                // literal [N x i8]* -> converte para i8* -> cria %String*
                String castTmp = temps.newTemp();
                llvm.append("  ").append(castTmp)
                        .append(" = bitcast ").append(type)
                        .append(" ").append(temp)
                        .append(" to i8*\n");

                strTmp = temps.newTemp();
                llvm.append("  ").append(strTmp)
                        .append(" = call %String* @createString(i8* ")
                        .append(castTmp).append(")\n");
            } else {
                throw new RuntimeException("Unsupported list element type for string list: " + type);
            }

            // adiciona o elemento convertido à lista
            llvm.append("  call void @arraylist_add_String(%ArrayList* ")
                    .append(listCast).append(", %String* ").append(strTmp).append(")\n");
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
