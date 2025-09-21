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

        //  registra todas as strings da lista antes de gerar qualquer código LLVM
        for (ASTNode element : node.getList().getElements()) {
            if (element instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                globalStrings.getOrCreateString((String) lit.value.getValue());
            }
        }

        //  aria a lista inicial
        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr).append(" = call i8* @arraylist_create(i64 4)\n");

        //  adiciona elementos
        for (ASTNode element : node.getList().getElements()) {
            String temp;
            String type;

            if (element instanceof LiteralNode lit) {
                Object val = lit.value.getValue();
                String valType = lit.value.getType();

                switch (valType) {
                    case "string" -> {
                        // String: mantém ponteiro
                        String strName = globalStrings.getOrCreateString((String) val);
                        int len = ((String) val).length() + 2;
                        temp = temps.newTemp();
                        llvm.append("  ").append(temp)
                                .append(" = bitcast [").append(len).append(" x i8]* ")
                                .append(strName).append(" to i8*\n");
                        type = "i8*";
                    }
                    case "int", "double", "boolean" -> {
                        // Boxar primitivos
                        type = switch (valType) {
                            case "int" -> "i32";
                            case "double" -> "double";
                            case "boolean" -> "i1";
                            default -> throw new RuntimeException("Tipo inesperado: " + valType);
                        };
                        String tmpAlloc = temps.newTemp();
                        llvm.append("  ").append(tmpAlloc).append(" = alloca ").append(type).append("\n");
                        String literalValue = switch (valType) {
                            case "int" -> val.toString();
                            case "double" -> val.toString();
                            case "boolean" -> ((Boolean) val) ? "1" : "0";
                            default -> throw new RuntimeException("Tipo inesperado: " + valType);
                        };
                        llvm.append("  store ").append(type).append(" ").append(literalValue)
                                .append(", ").append(type).append("* ").append(tmpAlloc).append("\n");
                        temp = temps.newTemp();
                        llvm.append("  ").append(temp).append(" = bitcast ").append(type).append("* ")
                                .append(tmpAlloc).append(" to i8*\n");
                        type = "i8*";
                    }
                    default -> throw new RuntimeException("Tipo não suportado na lista: " + valType);
                }
            } else {
                // expressões complexas ou outras variáveis
                String exprLLVM = element.accept(visitor);
                llvm.append(exprLLVM);
                temp = extractTemp(exprLLVM);

                // boxar valores primitivos para i8* também
                String elemType = extractType(exprLLVM);
                if (!elemType.equals("i8*")) {
                    String tmpAlloc = temps.newTemp();
                    llvm.append("  ").append(tmpAlloc).append(" = alloca ").append(elemType).append("\n");
                    llvm.append("  store ").append(elemType).append(" ").append(temp)
                            .append(", ").append(elemType).append("* ").append(tmpAlloc).append("\n");
                    temp = temps.newTemp();
                    llvm.append("  ").append(temp).append(" = bitcast ").append(elemType).append("* ")
                            .append(tmpAlloc).append(" to i8*\n");
                    type = "i8*";
                } else {
                    type = "i8*";
                }
            }

            llvm.append("  call void @setItems(i8* ").append(listPtr)
                    .append(", i8* ").append(temp).append(")\n");
        }

        //  retorna a lista
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
