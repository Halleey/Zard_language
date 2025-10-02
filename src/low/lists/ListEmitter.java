package low.lists;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVisitorMain;
import java.util.List;


public class ListEmitter {
    private final TempManager temps;

    public ListEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(ListNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        // Cria a lista temporária
        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr)
                .append(" = call i8* @arraylist_create(i64 ").append(Math.max(4, n)).append(")\n");

        for (ASTNode element : elements) {
            String elemLLVM = element.accept(visitor);
            llvm.append(elemLLVM);

            String temp = extractTemp(elemLLVM);
            String type = extractType(elemLLVM);

            switch (type) {
                case "i32" ->
                        llvm.append("  call void @arraylist_add_int(i8* ").append(listPtr)
                                .append(", i32 ").append(temp).append(")\n");
                case "double" ->
                        llvm.append("  call void @arraylist_add_double(i8* ").append(listPtr)
                                .append(", double ").append(temp).append(")\n");
                case "%String*", "i8*" -> {
                    String castTemp = temps.newTemp();
                    llvm.append("  ").append(castTemp)
                            .append(" = bitcast ").append(type).append(" ").append(temp).append(" to i8*\n");
                    llvm.append("  call void @arraylist_add_string(i8* ").append(listPtr)
                            .append(", i8* ").append(castTemp).append(")\n");
                }

                default -> throw new RuntimeException("Tipo de lista não suportado: " + type);
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
