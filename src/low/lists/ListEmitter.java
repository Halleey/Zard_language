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

        // adiciona cada elemento como %DynValue*
        for (ASTNode element : node.getList().getElements()) {
            String dvTmp;
            if (element instanceof LiteralNode lit) {
                Object val = lit.value.getValue();
                String valType = lit.value.getType();

                dvTmp = temps.newTemp();
                switch (valType) {
                    case "int" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createInt(i32 ").append(val).append(")\n");
                    case "double" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createDouble(double ").append(val).append(")\n");
                    case "boolean" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createBool(i1 ").append(((Boolean) val ? "1" : "0")).append(")\n");
                    case "string" -> {
                        String strName = globalStrings.getOrCreateString((String) val);
                        llvm.append("  ").append(dvTmp)
                                .append(" = call %DynValue* @createString(i8* getelementptr ([")
                                .append(((String) val).length() + 2).append(" x i8], [")
                                .append(((String) val).length() + 2).append(" x i8]* ")
                                .append(strName).append(", i32 0, i32 0))\n");
                    }
                    default -> throw new RuntimeException("Tipo não suportado na lista (literal): " + valType);
                }
            } else {
                // expressão complexa
                String exprLLVM = element.accept(visitor);
                llvm.append(exprLLVM);
                String tmp = extractTemp(exprLLVM);
                String elemType = extractType(exprLLVM);

                dvTmp = temps.newTemp();
                switch (elemType) {
                    case "i32" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createInt(i32 ").append(tmp).append(")\n");
                    case "double" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createDouble(double ").append(tmp).append(")\n");
                    case "i1" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createBool(i1 ").append(tmp).append(")\n");
                    case "i8*" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createString(i8* ").append(tmp).append(")\n");
                    default -> throw new RuntimeException("Tipo não suportado na lista (expressão): " + elemType);
                }
            }

            // adiciona à lista
            llvm.append("  call void @setItems(i8* ").append(listPtr).append(", %DynValue* ").append(dvTmp).append(")\n");
        }

        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");
        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int typeIdx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", typeIdx);
        return code.substring(typeIdx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
