package low.lists.generics;

import ast.lists.ListAddNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.lists.bool.ListBoolAddEmitter;
import low.lists.doubles.ListAddDoubleEmitter;
import low.lists.ints.ListIntAddEmitter;
import low.main.GlobalStringManager;
import low.module.LLVMEmitVisitor;
import low.module.LLVisitorMain;
public class ListAddEmitter {

    private final TempManager temps;
    private final GlobalStringManager globalStringManager;
    private final ListIntAddEmitter intAddEmitter;
    private final ListAddDoubleEmitter doubleEmitter;
    private final ListBoolAddEmitter boolAddEmitter;

    public ListAddEmitter(TempManager temps, GlobalStringManager globalStringManager) {
        this.temps = temps;
        this.globalStringManager = globalStringManager;
        this.intAddEmitter = new ListIntAddEmitter(temps);
        this.doubleEmitter = new ListAddDoubleEmitter(temps);
        this.boolAddEmitter = new ListBoolAddEmitter(temps);
    }

    public String emit(ListAddNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String specialized = null;
        if (visitor instanceof LLVisitorMain mainVisitor) {
            specialized = mainVisitor.getCurrentSpecializationType();
        }

        // =========================
        // MODO ESPECIALIZADO (Set<int>, Set<Item>, etc.)
        // =========================
        if (specialized != null) {

            String listCode = node.getListNode().accept(visitor);
            llvm.append(listCode);
            String listTmp = extractTemp(listCode);
            String listType = extractType(listCode); // agora pega o ÚLTIMO ;;TYPE:

            String valCode = node.getValuesNode().accept(visitor);
            llvm.append(valCode);
            String valTmp = extractTemp(valCode);
            String valType = extractType(valCode);

            System.out.println("[ListAddEmitter] (SPECIALIZED MODE)");
            System.out.println("  listCode:\n" + listCode);
            System.out.println("  → listTmp = " + listTmp);
            System.out.println("  → listType = " + listType);
            System.out.println("  valCode:\n" + valCode);
            System.out.println("  → valTmp = " + valTmp);
            System.out.println("  → valType = " + valType);

            String func = switch (specialized) {
                case "int"    -> "arraylist_add_int";
                case "double" -> "arraylist_add_double";
                case "bool", "boolean" -> "arraylist_add_bool";
                case "string" -> "arraylist_add_string"; // usado só pra log
                default       -> "arraylist_add_ptr";
            };

            System.out.println("[ListAddEmitter] Runtime function chosen = " + func);

            String listLLVMType;

            // ========== CASO: string ==========
            if (specialized.equals("string")) {
                // List<string> → %ArrayList* + arraylist_add_String(%String*)
                listLLVMType = "%ArrayList*";

                System.out.println("[ListAddEmitter] Specialized STRING branch acionado");

                // Se por algum motivo o campo não for %ArrayList*, faz bitcast
                if (!listType.equals("%ArrayList*")) {
                    String castList = temps.newTemp();
                    llvm.append("  ").append(castList)
                            .append(" = bitcast ")
                            .append(listType).append(" ").append(listTmp)
                            .append(" to %ArrayList*\n");
                    listTmp = castList;
                    listType = "%ArrayList*";
                }

                // valType deveria ser %String*
                if (!valType.equals("%String*")) {
                    // Se veio como i8* ou outra coisa, você poderia ajustar aqui se quiser
                    System.out.println("[ListAddEmitter][WARN] string especializada com valType=" + valType);
                }

                llvm.append("  call void @arraylist_add_String(%ArrayList* ")
                        .append(listTmp)
                        .append(", %String* ").append(valTmp)
                        .append(")\n");

                // ========== CASOS PRIMITIVOS: int/double/bool ==========
            } else if (specialized.equals("int") ||
                    specialized.equals("double") ||
                    specialized.equals("bool") ||
                    specialized.equals("boolean")) {

                String normalized = normalizeListType(specialized);
                listLLVMType = "%struct.ArrayList" + normalized + "*";

                System.out.println("[ListAddEmitter] normalized = " + normalized);
                System.out.println("[ListAddEmitter] listLLVMType = " + listLLVMType);

                String llvmType = mapToLLVMType(specialized);

                System.out.println("[ListAddEmitter] llvmType(value) = " + llvmType);

                // Se listType não bater com o tipo especializado, faz bitcast
                if (!listType.equals(listLLVMType)) {
                    String castList = temps.newTemp();
                    llvm.append("  ").append(castList)
                            .append(" = bitcast ")
                            .append(listType).append(" ").append(listTmp)
                            .append(" to ").append(listLLVMType).append("\n");
                    listTmp = castList;
                    listType = listLLVMType;
                }

                // Para primitivos, valType deve bater com llvmType; se não, poderia castar aqui também
                llvm.append("  call void @").append(func)
                        .append("(").append(listLLVMType).append(" ").append(listTmp)
                        .append(", ").append(llvmType).append(" ").append(valTmp)
                        .append(")\n");

                // ========== CASO GENÉRICO: qualquer outro tipo (Item, Pessoa, etc.) ==========
            } else {
                // Aqui queremos usar o ArrayList genérico de ponteiros
                listLLVMType = "%ArrayList*";

                System.out.println("[ListAddEmitter] Specialized PTR branch acionado para tipo=" + specialized);

                // listTmp deve ser %ArrayList*
                if (!listType.equals("%ArrayList*")) {
                    String castList = temps.newTemp();
                    llvm.append("  ").append(castList)
                            .append(" = bitcast ")
                            .append(listType).append(" ").append(listTmp)
                            .append(" to %ArrayList*\n");
                    listTmp = castList;
                    listType = "%ArrayList*";
                }

                // Agora garantimos que o valor é i8* via bitcast, se necessário
                String castValTmp = valTmp;
                if (!valType.equals("i8*")) {
                    String castVal = temps.newTemp();
                    llvm.append("  ").append(castVal)
                            .append(" = bitcast ")
                            .append(valType).append(" ").append(valTmp)
                            .append(" to i8*\n");
                    castValTmp = castVal;
                    valType = "i8*";
                }

                llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                        .append(listTmp)
                        .append(", i8* ").append(castValTmp)
                        .append(")\n");
            }

            // Marca o último valor/ tipo para encadear outras operações
            llvm.append(";;VAL:").append(listTmp)
                    .append(";;TYPE:").append(
                            specialized.equals("int") ||
                                    specialized.equals("double") ||
                                    specialized.equals("bool") ||
                                    specialized.equals("boolean")
                                    ? "%struct.ArrayList" + normalizeListType(specialized) + "*"
                                    : "%ArrayList*"
                    ).append("\n");

            System.out.println("[ListAddEmitter] ==== FIM DO SPECIALIZED ====\n");
            return llvm.toString();
        }

        // =========================
        // MODO FALLBACK (sem specialization)
        // =========================
        System.out.println("[ListAddEmitter] (FALLBACK MODE)");

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);
        String listTmp = extractTemp(listCode);
        String listType = extractType(listCode);

        String valCode = node.getValuesNode().accept(visitor);
        llvm.append(valCode);
        String valTmp = extractTemp(valCode);
        String valType = extractType(valCode);

        System.out.println("  listCode:\n" + listCode);
        System.out.println("  → listTmp = " + listTmp);
        System.out.println("  → listType = " + listType);
        System.out.println("  valCode:\n" + valCode);
        System.out.println("  → valTmp = " + valTmp);
        System.out.println("  → valType = " + valType);

        if (valType.equals("i32")) {
            System.out.println("[ListAddEmitter] → INT fallback");
            return intAddEmitter.emit(node, visitor);
        }
        if (valType.equals("double")) {
            System.out.println("[ListAddEmitter] → DOUBLE fallback");
            return doubleEmitter.emit(node, visitor);
        }
        if (valType.equals("i1")) {
            System.out.println("[ListAddEmitter] → BOOL fallback");
            return boolAddEmitter.emit(node, visitor);
        }

        System.out.println("[ListAddEmitter] → PTR/STRING fallback");

        String listCastTmp = temps.newTemp();
        llvm.append("  ").append(listCastTmp)
                .append(" = bitcast ").append(listType).append(" ").append(listTmp)
                .append(" to %ArrayList*\n");

        if (valType.equals("%String*")) {
            llvm.append("  call void @arraylist_add_String(%ArrayList* ")
                    .append(listCastTmp).append(", %String* ").append(valTmp).append(")\n");
        }
        else if (valType.equals("i8*")) {
            llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                    .append(listCastTmp).append(", i8* ").append(valTmp).append(")\n");
        }
        else {
            // qualquer outro ponteiro vira i8* via bitcast
            String castVal = temps.newTemp();
            llvm.append("  ").append(castVal)
                    .append(" = bitcast ").append(valType).append(" ").append(valTmp)
                    .append(" to i8*\n");

            llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                    .append(listCastTmp).append(", i8* ").append(castVal).append(")\n");
        }

        llvm.append(";;VAL:").append(listCastTmp).append(";;TYPE:%ArrayList*\n");

        System.out.println("[ListAddEmitter] ==== FIM FALBACK ====\n");
        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        // ANTES: usava primeiro ";;TYPE:" e podia pegar tipo errado.
        // AGORA: usa o ÚLTIMO ";;TYPE:" para casar com o último ;;VAL:.
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", lastTypeIdx);
        return code.substring(lastTypeIdx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }

    private String mapToLLVMType(String type) {
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "bool", "boolean" -> "i1";
            case "string" -> "%String*";
            default -> "i8*";
        };
    }

    private String normalizeListType(String type) {
        return switch (type) {
            case "boolean", "bool" -> "Bool";
            case "int" -> "Int";
            case "double" -> "Double";
            case "string", "String" -> "Str";
            default -> "Ptr";
        };
    }
}
