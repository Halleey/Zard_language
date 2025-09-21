package low.lists;

import ast.ASTNode;
import ast.lists.ListNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
public class ListEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public ListEmitter(TempManager temps, GlobalStringManager globalStrings) {
        this.temps = temps;
        this.globalStrings = globalStrings;
    }

    public String emit(ListNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        // 1️⃣ Registra todas as strings da lista antes de gerar qualquer código LLVM
        for (ASTNode element : node.getList().getElements()) {
            if (element instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                globalStrings.getOrCreateString((String) lit.value.getValue());
            }
        }

        // 2️⃣ Cria a lista inicial
        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr).append(" = call i8* @arraylist_create(i64 4)\n");

        // 3️⃣ Adiciona elementos
        for (ASTNode element : node.getList().getElements()) {
            String temp;
            String type;

            if (element instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                String strName = globalStrings.getOrCreateString((String) lit.value.getValue());
                int len = ((String) lit.value.getValue()).length() + 2;
                temp = temps.newTemp();
                llvm.append("  ").append(temp)
                        .append(" = bitcast [").append(len).append(" x i8]* ")
                        .append(strName).append(" to i8*\n");
                type = "i8*";
            } else {
                // Outros tipos (int, double, boolean, etc.)
                String exprLLVM = element.accept(visitor);
                llvm.append(exprLLVM);
                temp = extractTemp(exprLLVM);
                type = extractType(exprLLVM);
            }

            llvm.append("  call void @setItems(i8* ").append(listPtr)
                    .append(", ").append(type).append(" ").append(temp).append(")\n");
        }

        // 4️⃣ Retorna a lista
        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");
        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int typeIdx = code.indexOf(";;TYPE:");
        if (typeIdx == -1) throw new RuntimeException("Não encontrou ;;TYPE: em: " + code);
        int endIdx = code.indexOf("\n", typeIdx);
        return code.substring(typeIdx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
