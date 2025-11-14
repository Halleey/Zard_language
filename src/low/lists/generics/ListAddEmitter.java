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

        if (specialized != null) {

            // ---- Gerar código para acessar a lista ----
            String listCode = node.getListNode().accept(visitor);
            llvm.append(listCode);
            String listTmp = extractTemp(listCode);

            String valCode = node.getValuesNode().accept(visitor);
            llvm.append(valCode);
            String valTmp = extractTemp(valCode);

            String func = switch (specialized) {
                case "int"    -> "arraylist_add_int";
                case "double" -> "arraylist_add_double";
                case "bool", "boolean" -> "arraylist_add_bool";
                case "string" -> "arraylist_add_string";
                default       -> "arraylist_add_ptr";
            };

            String listLLVMType;

            if (specialized.equals("string")) {
                listLLVMType = "%ArrayList*";

                llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                        .append(listTmp)
                        .append(", %String* ").append(valTmp)
                        .append(")\n");

            } else {


                String normalized = normalizeListType(specialized);
                listLLVMType = "%struct.ArrayList" + normalized + "*";

                llvm.append("  call void @").append(func)
                        .append("(").append(listLLVMType).append(" ").append(listTmp)
                        .append(", ").append(mapToLLVMType(specialized)).append(" ").append(valTmp)
                        .append(")\n");
            }

            llvm.append(";;VAL:").append(listTmp)
                    .append(";;TYPE:").append(listLLVMType).append("\n");

            return llvm.toString();
        }

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);

        String valCode = node.getValuesNode().accept(visitor);
        llvm.append(valCode);

        String valType = extractType(valCode);

        if (valType.equals("i32"))    return intAddEmitter.emit(node, visitor);
        if (valType.equals("double")) return doubleEmitter.emit(node, visitor);
        if (valType.equals("i1"))     return boolAddEmitter.emit(node, visitor);

        String listTmp = extractTemp(listCode);

        String listCastTmp = temps.newTemp();
        llvm.append("  ").append(listCastTmp)
                .append(" = bitcast i8* ").append(listTmp)
                .append(" to %ArrayList*\n");

        String valTmp = extractTemp(valCode);

        if (valType.equals("%String*")) {
            llvm.append("  call void @arraylist_add_String(%ArrayList* ")
                    .append(listCastTmp).append(", %String* ").append(valTmp).append(")\n");
        }
        else if (valType.equals("i8*")) {
            if (node.getValuesNode() instanceof LiteralNode lit &&
                    lit.value.type().equals("string")) {

                String literal = (String) lit.value.value();
                String strName = globalStringManager.getOrCreateString(literal);

                llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                        .append(listCastTmp)
                        .append(", i8* getelementptr ([")
                        .append(literal.length() + 1)
                        .append(" x i8], [")
                        .append(literal.length() + 1)
                        .append(" x i8]* ")
                        .append(strName)
                        .append(", i32 0, i32 0))\n");

            } else {
                llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                        .append(listCastTmp).append(", i8* ").append(valTmp).append(")\n");
            }
        }
        else {

            llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                    .append(listCastTmp).append(", ")
                    .append(valType).append(" ").append(valTmp).append(")\n");
        }

        llvm.append(";;VAL:").append(listCastTmp).append(";;TYPE:%ArrayList*\n");
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
