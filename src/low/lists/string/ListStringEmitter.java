package low.lists.string;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVisitorMain;

import java.util.List;

public class ListStringEmitter {

    private final TempManager temps;

    public ListStringEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListNode node, LLVisitorMain visitor) {

        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        StringBuilder llvm = new StringBuilder();

        String rawPtr = temps.newTemp();

        llvm.append("  ").append(rawPtr)
                .append(" = call i8* @arraylist_create(i64 ")
                .append(Math.max(4, n))
                .append(")\n");

        String listPtr = temps.newTemp();

        llvm.append("  ").append(listPtr)
                .append(" = bitcast i8* ")
                .append(rawPtr)
                .append(" to %ArrayListString*\n");

        for (ASTNode element : elements) {

            String elemLLVM = element.accept(visitor);
            llvm.append(elemLLVM);

            String temp = extractTemp(elemLLVM);
            String type = extractType(elemLLVM);

            String strTmp;

            if (type.equals("%String*")) {

                strTmp = temp;

            }
            // literal i8*
            else if (type.equals("i8*")) {
                strTmp = temps.newTemp();
                llvm.append("  ").append(strTmp)
                        .append(" = call %String* @createString(i8* ")
                        .append(temp)
                        .append(")\n");
            }
            else if (type.matches("\\[\\d+ x i8\\]\\*")) {

                String castTmp = temps.newTemp();

                llvm.append("  ").append(castTmp)
                        .append(" = bitcast ")
                        .append(type)
                        .append(" ")
                        .append(temp)
                        .append(" to i8*\n");

                strTmp = temps.newTemp();
                llvm.append("  ").append(strTmp)
                        .append(" = call %String* @createString(i8* ")
                        .append(castTmp)
                        .append(")\n");
            }
            else {
                throw new RuntimeException(
                        "Invalid element for List<string>: " + type
                );
            }

            llvm.append("  call void @arraylist_string_add(%ArrayListString* ")
                    .append(listPtr)
                    .append(", %String* ")
                    .append(strTmp)
                    .append(")\n");
        }

        llvm.append(";;VAL:")
                .append(listPtr)
                .append(";;TYPE:%ArrayListString*\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        int endIdx = code.indexOf(";;TYPE:", idx);

        if (idx == -1 || endIdx == -1)
            throw new RuntimeException("Invalid LLVM marker");

        return code.substring(idx + 6, endIdx).trim();
    }

    private String extractType(String code) {
        int idx = code.lastIndexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", idx);

        if (idx == -1)
            throw new RuntimeException("Invalid LLVM marker");

        if (endIdx == -1)
            endIdx = code.length();

        return code.substring(idx + 7, endIdx).trim();
    }
}