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

        // registra strings literais para o header
        for (ASTNode element : node.getList().getElements()) {
            if (element instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                globalStrings.getOrCreateString((String) lit.value.getValue());
            }
        }

        // cria a lista
        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr).append(" = call i8* @arraylist_create(i64 4)\n");

        // adiciona cada elemento (sempre como i8* que aponta para DynValue)
        for (ASTNode element : node.getList().getElements()) {
            String dvTemp; // será i8* (DynValue*)
            if (element instanceof LiteralNode lit) {
                Object val = lit.value.getValue();
                String valType = lit.value.getType();

                switch (valType) {
                    case "int" -> {
                        dvTemp = temps.newTemp();
                        llvm.append("  ").append(dvTemp).append(" = call i8* @createInt(i32 ")
                                .append(val.toString()).append(")\n");
                    }
                    case "double" -> {
                        dvTemp = temps.newTemp();
                        llvm.append("  ").append(dvTemp).append(" = call i8* @createDouble(double ")
                                .append(val.toString()).append(")\n");
                    }
                    case "boolean" -> {
                        String b = ((Boolean) val) ? "1" : "0";
                        dvTemp = temps.newTemp();
                        llvm.append("  ").append(dvTemp).append(" = call i8* @createBool(i1 ")
                                .append(b).append(")\n");
                    }
                    case "string" -> {
                        String strName = globalStrings.getOrCreateString((String) val);
                        // passar ponteiro para a string literal
                        dvTemp = temps.newTemp();
                        llvm.append("  ").append(dvTemp)
                                .append(" = call i8* @createString(i8* getelementptr (")
                                .append("[").append(((String) val).length() + 2).append(" x i8], ")
                                .append("[").append(((String) val).length() + 2).append(" x i8]* ")
                                .append(strName).append(", i32 0, i32 0))\n");
                    }
                    default -> throw new RuntimeException("Tipo não suportado na lista (literal): " + valType);
                }
            } else {
                // expressão - gera o código da expressão e depois "boxa" em DynValue via chamadas create*
                String exprLLVM = element.accept(visitor);
                llvm.append(exprLLVM); // inclui o código que produz ;;VAL:tmp;;TYPE:tipo
                String tmp = extractTemp(exprLLVM);
                String elemType = extractType(exprLLVM);

                // Se já for i8* (ex: variável string ou outra lista), assumimos que é DynValue* ou string ptr.
                if ("i8*".equals(elemType)) {
                    // Pode ser ponteiro para string (não DynValue) — mas se for variável string,
                    // criamos createString? Na prática, o caso de expressão que produz i8* normalmente
                    // será string literal/var; então criamos DynValue com createString.
                    String dv = temps.newTemp();
                    llvm.append("  ").append(dv).append(" = call i8* @createString(i8* ").append(tmp).append(")\n");
                    dvTemp = dv;
                } else if ("i32".equals(elemType)) {
                    dvTemp = temps.newTemp();
                    llvm.append("  ").append(dvTemp).append(" = call i8* @createInt(i32 ").append(tmp).append(")\n");
                } else if ("double".equals(elemType)) {
                    dvTemp = temps.newTemp();
                    llvm.append("  ").append(dvTemp).append(" = call i8* @createDouble(double ").append(tmp).append(")\n");
                } else if ("i1".equals(elemType)) {
                    // tmp é i1, createBool espera i1
                    dvTemp = temps.newTemp();
                    llvm.append("  ").append(dvTemp).append(" = call i8* @createBool(i1 ").append(tmp).append(")\n");
                } else {
                    throw new RuntimeException("Tipo de expressão não suportado na lista: " + elemType);
                }
            }

            // setItems(listPtr, dvTemp)
            llvm.append("  call void @setItems(i8* ").append(listPtr).append(", i8* ").append(dvTemp).append(")\n");
        }

        // retorno
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
