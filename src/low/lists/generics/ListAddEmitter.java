package low.lists.generics;

import ast.lists.ListAddNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.lists.bool.ListBoolAddEmitter;
import low.lists.doubles.ListAddDoubleEmitter;
import low.lists.ints.ListIntAddEmitter;
import low.main.GlobalStringManager;
import low.module.LLVMEmitVisitor;


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

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);

        String valCode = node.getValuesNode().accept(visitor);
        String valType = extractType(valCode);

        if (valType.equals("i32")) {
            return intAddEmitter.emit(node, visitor);
        }
        if (valType.equals("double")) {
            return doubleEmitter.emit(node, visitor);
        }
        if (valType.equals("i1")) {
            return boolAddEmitter.emit(node, visitor);
        }

        String listTmp = extractTemp(listCode);

        String listCastTmp = temps.newTemp();
        llvm.append("  ").append(listCastTmp)
                .append(" = bitcast i8* ").append(listTmp)
                .append(" to %ArrayList*\n");

        llvm.append(valCode);
        String valTmp = extractTemp(valCode);

        switch (valType) {
            case "%String*" -> {

                llvm.append("  call void @arraylist_add_String(%ArrayList* ")
                        .append(listCastTmp).append(", %String* ").append(valTmp).append(")\n");
            }
            case "i8*" -> {

                if (node.getValuesNode() instanceof LiteralNode lit &&
                        lit.value.type().equals("string")) {
                    String literal = (String) lit.value.value();
                    String strName = globalStringManager.getOrCreateString(literal);
                    llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                            .append(listCastTmp).append(", i8* getelementptr ([")
                            .append(literal.length() + 1).append(" x i8], [")
                            .append(literal.length() + 1).append(" x i8]* ")
                            .append(strName).append(", i32 0, i32 0))\n");
                } else {
                    llvm.append("  call void @arraylist_add_string(%ArrayList* ")
                            .append(listCastTmp).append(", i8* ").append(valTmp).append(")\n");
                }
            }
            default -> {

                if (valType.startsWith("%struct.") || valType.startsWith("%")) {
                    llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                            .append(listCastTmp).append(", ").append(valType).append(" ").append(valTmp).append(")\n");
                } else {
                    throw new RuntimeException("Tipo não suportado em ListAdd: " + valType);
                }
            }

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
}
