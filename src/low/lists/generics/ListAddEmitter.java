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

        // 1) avalia lista
        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);
        String listTmp  = extractTemp(listCode);
        String listType = extractType(listCode); // tipo REAL da lista no IR

        // 2) avalia valor
        String valCode = node.getValuesNode().accept(visitor);
        llvm.append(valCode);
        String valTmp  = extractTemp(valCode);
        String valType = extractType(valCode);

        // 3) decide pelo tipo da LISTA (e não pela specialization)
        switch (listType) {

            case "%struct.ArrayListInt*": {
                // valor deve ser i32
                if (!valType.equals("i32")) {
                    throw new RuntimeException("List<int>.add recebeu " + valType + " (esperado i32)");
                }
                llvm.append("  call void @arraylist_add_int(%struct.ArrayListInt* ")
                        .append(listTmp).append(", i32 ").append(valTmp).append(")\n");
                llvm.append(";;VAL:").append(listTmp).append(";;TYPE:%struct.ArrayListInt*\n");
                return llvm.toString();
            }

            case "%struct.ArrayListDouble*": {
                // valor deve ser double
                if (!valType.equals("double")) {
                    throw new RuntimeException("List<double>.add recebeu " + valType + " (esperado double)");
                }
                llvm.append("  call void @arraylist_add_double(%struct.ArrayListDouble* ")
                        .append(listTmp).append(", double ").append(valTmp).append(")\n");
                llvm.append(";;VAL:").append(listTmp).append(";;TYPE:%struct.ArrayListDouble*\n");
                return llvm.toString();
            }

            case "%struct.ArrayListBool*": {
                // valor deve ser i1
                if (!valType.equals("i1")) {
                    throw new RuntimeException("List<bool>.add recebeu " + valType + " (esperado i1)");
                }
                llvm.append("  call void @arraylist_add_bool(%struct.ArrayListBool* ")
                        .append(listTmp).append(", i1 ").append(valTmp).append(")\n");
                llvm.append(";;VAL:").append(listTmp).append(";;TYPE:%struct.ArrayListBool*\n");
                return llvm.toString();
            }

            case "%ArrayList*": {

                if (valType.equals("i32") || valType.equals("i1") || valType.equals("double")) {
                    throw new RuntimeException(
                            "Tentando adicionar primitivo (" + valType + ") em List<T>. Use lista tipada."
                    );
                }

                if (valType.equals("%String*")) {
                    llvm.append("  call void @arraylist_add_String(%ArrayList* ")
                            .append(listTmp).append(", %String* ").append(valTmp).append(")\n");
                    llvm.append(";;VAL:").append(listTmp).append(";;TYPE:%ArrayList*\n");
                    return llvm.toString();
                }

                String asI8 = valTmp;

                if (!valType.equals("i8*")) {
                    String castVal = temps.newTemp();
                    llvm.append("  ").append(castVal)
                            .append(" = bitcast ").append(valType).append(" ").append(valTmp)
                            .append(" to i8*\n");
                    asI8 = castVal;
                }

                llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                        .append(listTmp).append(", i8* ").append(asI8).append(")\n");
                llvm.append(";;VAL:").append(listTmp).append(";;TYPE:%ArrayList*\n");
                return llvm.toString();
            }


            default: {
                // Se cair aqui, o problema está em quem emite o tipo da lista (FieldAccess/ListInit)
                throw new RuntimeException("Tipo de lista inesperado no IR: " + listType);
            }
        }
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", lastTypeIdx);
        return code.substring(lastTypeIdx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
