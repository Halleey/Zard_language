package low.lists;

import ast.lists.ListAddNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.LLVMEmitVisitor;


public class ListAddEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStringManager;

    public ListAddEmitter(TempManager temps, GlobalStringManager globalStringManager) {
        this.temps = temps;
        this.globalStringManager = globalStringManager;
    }

    public String emit(ListAddNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        // avalia a lista
        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);
        String listTmp = extractTemp(listCode);

        // avalia o valor
        String valCode = node.getValuesNode().accept(visitor);
        llvm.append(valCode);
        String valTmp = extractTemp(valCode);
        String valType = extractType(valCode);

        String dvTmp = temps.newTemp();
        switch (valType) {
            case "i32" -> llvm.append("  ").append(dvTmp)
                    .append(" = call %DynValue* @createInt(i32 ").append(valTmp).append(")\n");
            case "double" -> llvm.append("  ").append(dvTmp)
                    .append(" = call %DynValue* @createDouble(double ").append(valTmp).append(")\n");
            case "i1" -> llvm.append("  ").append(dvTmp)
                    .append(" = call %DynValue* @createBool(i1 ").append(valTmp).append(")\n");
            case "i8*" -> {
                if (node.getValuesNode() instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                    String literal = (String) lit.value.getValue();
                    String strName = globalStringManager.getOrCreateString(literal);
                    llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createString(i8* getelementptr ([")
                            .append(literal.length() + 2).append(" x i8], [")
                            .append(literal.length() + 2).append(" x i8]* ")
                            .append(strName).append(", i32 0, i32 0))\n");
                } else {
                    llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createString(i8* ").append(valTmp).append(")\n");
                }
            }
            default -> throw new RuntimeException("Tipo não suportado em ListAdd: " + valType);
        }

        // adiciona à lista
        llvm.append("  call void @setItems(i8* ").append(listTmp).append(", %DynValue* ").append(dvTmp).append(")\n");

        llvm.append(";;VAL:").append(dvTmp).append(";;TYPE:%DynValue*\n");
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
