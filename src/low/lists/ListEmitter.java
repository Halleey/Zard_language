package low.lists;

import ast.ASTNode;
import ast.lists.ListNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;

import java.util.List;

public class ListEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public ListEmitter(TempManager temps, GlobalStringManager globalStrings) {
        this.temps = temps;
        this.globalStrings = globalStrings;
    }
    public String emit(ListNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();
        List<ASTNode> elements = node.getList().getElements();
        int n = elements.size();

        // Cria a lista (capacidade inicial = max(4, n))
        String listPtr = temps.newTemp();
        llvm.append("  ").append(listPtr)
                .append(" = call i8* @arraylist_create(i64 ").append(Math.max(4, n)).append(")\n");

        if (n == 0) {
            llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");
            return llvm.toString();
        }

        // Array temporário de DynValue* ([n x %DynValue*])
        String arrTmp = temps.newTemp();
        llvm.append("  ").append(arrTmp).append(" = alloca [")
                .append(n).append(" x %DynValue*]\n");

        // Preenche o array temporário
        for (int i = 0; i < n; i++) {
            ASTNode element = elements.get(i);
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
                            .append(" = call %DynValue* @createBool(i1 ")
                            .append(((Boolean) val) ? "1" : "0").append(")\n");
                    case "string" -> {
                        String strName = globalStrings.getOrCreateString((String) val);
                        int len = ((String) val).length() + 1;
                        llvm.append("  ").append(dvTmp)
                                .append(" = call %DynValue* @createString(i8* getelementptr ([")
                                .append(len).append(" x i8], [")
                                .append(len).append(" x i8]* ")
                                .append(strName).append(", i32 0, i32 0))\n");
                    }
                    default -> throw new RuntimeException("Tipo não suportado na lista (literal): " + valType);
                }
            } else {
                // Expressão complexa
                String code = element.accept(visitor);
                llvm.append(code);
                String tmp = extractTemp(code);
                String type = extractType(code);

                dvTmp = temps.newTemp();
                switch (type) {
                    case "i32" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createInt(i32 ").append(tmp).append(")\n");
                    case "double" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createDouble(double ").append(tmp).append(")\n");
                    case "i1" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createBool(i1 ").append(tmp).append(")\n");
                    case "i8*" -> llvm.append("  ").append(dvTmp)
                            .append(" = call %DynValue* @createString(i8* ").append(tmp).append(")\n");
                    default -> throw new RuntimeException("Tipo não suportado na lista (expressão): " + type);
                }
            }

            // Armazena no array temporário usando ponteiro para o elemento
            String ptrTmp = temps.newTemp();
            llvm.append("  ").append(ptrTmp)
                    .append(" = getelementptr inbounds [").append(n).append(" x %DynValue*], [")
                    .append(n).append(" x %DynValue*]* ").append(arrTmp)
                    .append(", i32 0, i32 ").append(i).append("\n");

            llvm.append("  store %DynValue* ").append(dvTmp)
                    .append(", %DynValue** ").append(ptrTmp).append("\n");
        }

        // Cria ponteiro para o primeiro elemento para passar para addAll
        String firstElemPtr = temps.newTemp();
        llvm.append("  ").append(firstElemPtr)
                .append(" = getelementptr inbounds [").append(n).append(" x %DynValue*], [")
                .append(n).append(" x %DynValue*]* ").append(arrTmp)
                .append(", i32 0, i32 0\n");

        // Chama addAll apenas uma vez
        String listTyped = temps.newTemp();
        llvm.append("  ").append(listTyped)
                .append(" = bitcast i8* ").append(listPtr).append(" to %ArrayList*\n");

        llvm.append("  call void @addAll(%ArrayList* ").append(listTyped)
                .append(", %DynValue** ").append(firstElemPtr)
                .append(", i64 ").append(n).append(")\n");


        // Retorna a lista como valor final
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