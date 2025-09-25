package low.lists;

import ast.ASTNode;
import ast.lists.ListAddAllNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.LLVMEmitVisitor;

public class ListAddAllEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStringManager;

    public ListAddAllEmitter(TempManager temps, GlobalStringManager globalStringManager) {
        this.temps = temps;
        this.globalStringManager = globalStringManager;
    }

    public String emit(ListAddAllNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        // Avalia a lista de destino
        String listCode = node.getTargetListNode().accept(visitor);
        llvm.append(listCode);
        String listTmp = extractTemp(listCode);

        // Avalia todos os valores a adicionar
        for (ASTNode valueNode : node.getArgs()) {
            String valCode = valueNode.accept(visitor);
            llvm.append(valCode);

            String valTmp = extractTemp(valCode);
            String valType = extractType(valCode);

            String dvTmp;

            switch (valType) {
                case "i32" -> {
                    dvTmp = temps.newTemp();
                    llvm.append("  ").append(dvTmp)
                            .append(" = call i8* @createInt(i32 ").append(valTmp).append(")\n");
                }
                case "double" -> {
                    dvTmp = temps.newTemp();
                    llvm.append("  ").append(dvTmp)
                            .append(" = call i8* @createDouble(double ").append(valTmp).append(")\n");
                }
                case "i1" -> {
                    dvTmp = temps.newTemp();
                    llvm.append("  ").append(dvTmp)
                            .append(" = call i8* @createBool(i1 ").append(valTmp).append(")\n");
                }
                case "i8*" -> {
                    dvTmp = temps.newTemp();
                    if (valueNode instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                        String literal = (String) lit.value.getValue();
                        String strName = globalStringManager.getOrCreateString(literal);
                        llvm.append("  ").append(dvTmp)
                                .append(" = call i8* @createString(i8* getelementptr ([")
                                .append(literal.length() + 2).append(" x i8], [")
                                .append(literal.length() + 2).append(" x i8]* ")
                                .append(strName).append(", i32 0, i32 0))\n");
                    } else {
                        llvm.append("  ").append(dvTmp)
                                .append(" = call i8* @createString(i8* ").append(valTmp).append(")\n");
                    }
                }
                default -> throw new RuntimeException("Tipo não suportado em ListAddAll: " + valType);
            }

            llvm.append("  call void @setItems(i8* ").append(listTmp)
                    .append(", i8* ").append(dvTmp).append(")\n");

            llvm.append(";;VAL:").append(dvTmp).append(";;TYPE:i8*\n");
        }

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
