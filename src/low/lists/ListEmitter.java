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
                case "i32" -> llvm.append("  call void @arraylist_add_int(i8* ")
                        .append(listPtr).append(", i32 ").append(temp).append(")\n");

                case "double" -> llvm.append("  call void @arraylist_add_double(i8* ")
                        .append(listPtr).append(", double ").append(temp).append(")\n");

                case "%String*" -> {
                    // pega campo .data da struct %String* (i8*)
                    String ptrField = temps.newTemp(); // i8**
                    llvm.append("  ").append(ptrField)
                            .append(" = getelementptr inbounds %String, %String* ")
                            .append(temp).append(", i32 0, i32 0\n");
                    String loaded = temps.newTemp(); // i8*
                    llvm.append("  ").append(loaded)
                            .append(" = load i8*, i8** ").append(ptrField).append("\n");
                    llvm.append("  call void @arraylist_add_string(i8* ")
                            .append(listPtr).append(", i8* ").append(loaded).append(")\n");
                }

                case "i8*" -> llvm.append("  call void @arraylist_add_string(i8* ")
                        .append(listPtr).append(", i8* ").append(temp).append(")\n");

                default -> {
                    // Caso seja literal global [N x i8]* ou outro literal similar
                    if (type.matches("\\[\\d+ x i8\\]\\*")) {
                        System.out.println("ENTRANDO AQUI NO DEFAULT");
                        String castTmp = temps.newTemp();
                        llvm.append("  ").append(castTmp)
                                .append(" = bitcast ").append(type).append(" ").append(temp)
                                .append(" to i8*\n");
                        System.out.println("CONFERINDO RESULTADO" + castTmp);
                        llvm.append("  call void @arraylist_add_string(i8* ")
                                .append(listPtr).append(", i8* ").append(castTmp).append(")\n");
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
