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

        int argCount = node.getArgs().size();
        if (argCount == 0) return llvm.toString();

        // Cria array de %DynValue* na stack
        String arrayTmp = temps.newTemp();
        llvm.append("  ").append(arrayTmp).append(" = alloca %DynValue*, i64 ").append(argCount).append("\n");

        // Avalia todos os valores e armazena no array
        int index = 0;
        for (ASTNode valueNode : node.getArgs()) {
            String valCode = valueNode.accept(visitor);
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
                    if (valueNode instanceof LiteralNode lit && lit.value.getType().equals("string")) {
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
                default -> throw new RuntimeException("Tipo não suportado em ListAddAll: " + valType);
            }

            // GEP para a posição do array
            String gepTmp = temps.newTemp();
            llvm.append("  ").append(gepTmp).append(" = getelementptr inbounds %DynValue*, %DynValue** ")
                    .append(arrayTmp).append(", i64 ").append(index).append("\n");

            // Store no array
            llvm.append("  store %DynValue* ").append(dvTmp).append(", %DynValue** ").append(gepTmp).append("\n");

            index++;
        }

        // Cast da lista de i8* para %ArrayList*
        String listCastTmp = temps.newTemp();
        llvm.append("  ").append(listCastTmp).append(" = bitcast i8* ").append(listTmp).append(" to %ArrayList*\n");

        llvm.append("  call void @addAll(%ArrayList* ").append(listCastTmp)
                .append(", %DynValue** ").append(arrayTmp)
                .append(", i64 ").append(argCount).append(")\n");

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
