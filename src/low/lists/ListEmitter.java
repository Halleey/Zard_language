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

        // 1. Cria a lista
        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr)
                .append(" = call i8* @arraylist_create(i64 ").append(Math.max(4, n)).append(")\n");

        // bitcast para %ArrayList* uma vez
        String listCast = temps.newTemp();
        llvm.append("  ").append(listCast)
                .append(" = bitcast i8* ").append(listPtr).append(" to %ArrayList*\n");

        // 2. Adiciona elementos
        for (ASTNode element : elements) {
            String elemLLVM = element.accept(visitor);
            llvm.append(elemLLVM);

            String temp = extractTemp(elemLLVM);
            String type = extractType(elemLLVM);

            switch (type) {
                case "i32" -> llvm.append("  call void @arraylist_add_int(%ArrayList* ")
                        .append(listCast).append(", i32 ").append(temp).append(")\n");

                case "double" -> llvm.append("  call void @arraylist_add_double(%ArrayList* ")
                        .append(listCast).append(", double ").append(temp).append(")\n");

                case "%String*" -> {
                    // pega o i8* do struct String
                    String loaded = temps.newTemp();
                    llvm.append("  ").append(loaded)
                            .append(" = getelementptr inbounds %String, %String* ")
                            .append(temp).append(", i32 0, i32 0\n");

                    String realData = temps.newTemp();
                    llvm.append("  ").append(realData)
                            .append(" = load i8*, i8** ").append(loaded).append("\n");

                    llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                            .append(listCast).append(", i8* ").append(realData).append(")\n");
                }

                case "i8*" -> llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                        .append(listCast).append(", i8* ").append(temp).append(")\n");

                default -> {
                    // trata literais globais [N x i8]* ou similares
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
