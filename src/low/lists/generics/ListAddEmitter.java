package low.lists.generics;

import ast.lists.ListAddNode;
import low.TempManager;
import low.lists.bool.ListBoolAddEmitter;
import low.lists.doubles.ListAddDoubleEmitter;
import low.lists.ints.ListIntAddEmitter;
import low.lists.string.ListAddStringEmitter;
import low.main.GlobalStringManager;
import low.module.LLVMEmitVisitor;

public class ListAddEmitter {

    private final TempManager temps;

    private final ListIntAddEmitter intAddEmitter;
    private final ListAddDoubleEmitter doubleEmitter;
    private final ListBoolAddEmitter boolAddEmitter;
    private final ListAddStringEmitter stringEmitter;

    public ListAddEmitter(TempManager temps, GlobalStringManager gsm) {
        this.temps = temps;

        this.intAddEmitter = new ListIntAddEmitter(temps);
        this.doubleEmitter = new ListAddDoubleEmitter(temps);
        this.boolAddEmitter = new ListBoolAddEmitter(temps);
        this.stringEmitter = new ListAddStringEmitter();
    }

    public String emit(ListAddNode node, LLVMEmitVisitor visitor) {

        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        llvm.append(listCode);

        String listTmp = extractTemp(listCode);
        String listType = extractType(listCode);

        String valCode = node.getValuesNode().accept(visitor);
        llvm.append(valCode);

        String valTmp = extractTemp(valCode);
        String valType = extractType(valCode);

        if (listType.equals("%ArrayListString*")) {
            return llvm + stringEmitter.emit(listTmp, listType, valTmp, valType);
        }

        switch (valType) {
            case "i32" -> {
                return llvm + intAddEmitter.emit(node, visitor);
            }
            case "double" -> {
                return llvm + doubleEmitter.emit(node, visitor);
            }
            case "i1" -> {
                return llvm + boolAddEmitter.emit(node, visitor);
            }
        }

        if (listType.equals("%ArrayList*")) {

            String castValTmp = valTmp;

            if (!valType.equals("i8*")) {
                String castVal = temps.newTemp();
                llvm.append("  ").append(castVal)
                        .append(" = bitcast ")
                        .append(valType).append(" ").append(valTmp)
                        .append(" to i8*\n");

                castValTmp = castVal;
            }

            llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                    .append(listTmp)
                    .append(", i8* ")
                    .append(castValTmp)
                    .append(")\n");

            llvm.append(";;VAL:")
                    .append(listTmp)
                    .append(";;TYPE:%ArrayList*\n");

            return llvm.toString();
        }

        throw new RuntimeException(
                "[ListAddEmitter] Unsupported combination:\n" +
                        "ListType: " + listType + "\n" +
                        "ValueType: " + valType
        );
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        if (lastTypeIdx == -1) {
            throw new RuntimeException("[LLVM] TYPE marker not found:\n" + code);
        }
        int endIdx = code.indexOf("\n", lastTypeIdx);
        if (endIdx == -1) endIdx = code.length();
        return code.substring(lastTypeIdx + 7, endIdx).trim();
    }
}