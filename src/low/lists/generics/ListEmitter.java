package low.lists.generics;

import ast.ASTNode;
import ast.lists.ListNode;
import ast.variables.VariableNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import context.statics.symbols.UnknownType;
import low.TempManager;
import low.lists.bool.ListBoolEmitter;
import low.lists.doubles.ListDoubleEmitter;
import low.lists.ints.IntListEmitter;
import low.module.LLVisitorMain;
import java.util.List;


public class ListEmitter {

    private final TempManager temps;
    private final IntListEmitter intEmitter;
    private final ListDoubleEmitter doubleEmitter;
    private final ListBoolEmitter boolEmitter;

    public ListEmitter(TempManager temps) {
        this.temps = temps;
        this.intEmitter = new IntListEmitter(temps);
        this.doubleEmitter = new ListDoubleEmitter(temps);
        this.boolEmitter = new ListBoolEmitter(temps);
    }

    public String emit(ListNode node, LLVisitorMain visitor) {
        System.out.println("não entrou aqui");
        Type elementType = node.getList().getElementType();


        System.out.println("basic debug " + elementType);

        if (elementType instanceof PrimitiveTypes prim) {

            if (prim == PrimitiveTypes.INT) {
                return intEmitter.emit(node, visitor);
            }

            if (prim == PrimitiveTypes.DOUBLE) {
                return doubleEmitter.emit(node, visitor);
            }

            if (prim == PrimitiveTypes.BOOL) {
                System.out.println("entrou aqui na lista de boolean ");
                return boolEmitter.emit(node, visitor);
            }
        }

        if (elementType instanceof StructType) {

            StringBuilder llvm = new StringBuilder();
            var elements = node.getList().getElements();
            int n = elements.size();

            String listPtr = temps.newTemp();

            llvm.append("  ").append(listPtr)
                    .append(" = call i8* @arraylist_create(i64 ")
                    .append(Math.max(4, n)).append(")\n");

            String listCast = temps.newTemp();

            llvm.append("  ").append(listCast)
                    .append(" = bitcast i8* ")
                    .append(listPtr)
                    .append(" to %ArrayList*\n");

            for (ASTNode element : elements) {

                String elemLLVM = element.accept(visitor);
                llvm.append(elemLLVM);

                String temp = extractTemp(elemLLVM);
                String type = extractType(elemLLVM);

                llvm.append("  call void @arraylist_add_ptr(%ArrayList* ")
                        .append(listCast)
                        .append(", ")
                        .append(type)
                        .append(" ")
                        .append(temp)
                        .append(")\n");
            }

            llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");

            return llvm.toString();
        }

        // ===== STRING LIST =====

        StringBuilder llvm = new StringBuilder();
        var elements = node.getList().getElements();
        int n = elements.size();

        String listPtr = temps.newTemp();

        llvm.append("  ").append(listPtr)
                .append(" = call i8* @arraylist_create(i64 ")
                .append(Math.max(4, n)).append(")\n");

        String listCast = temps.newTemp();

        llvm.append("  ").append(listCast)
                .append(" = bitcast i8* ")
                .append(listPtr)
                .append(" to %ArrayList*\n");

        for (ASTNode element : elements) {

            String elemLLVM = element.accept(visitor);
            llvm.append(elemLLVM);

            String temp = extractTemp(elemLLVM);
            String type = extractType(elemLLVM);

            String strTmp;

            if (type.equals("%String*")) {
                strTmp = temp;
            }

            else if (type.equals("i8*")) {

                strTmp = temps.newTemp();

                llvm.append("  ").append(strTmp)
                        .append(" = call %String* @createString(i8* ")
                        .append(temp)
                        .append(")\n");
            }

            else if (type.matches("\\[\\d+ x i8\\]\\*")) {

                String castTmp = temps.newTemp();

                llvm.append("  ").append(castTmp)
                        .append(" = bitcast ")
                        .append(type)
                        .append(" ")
                        .append(temp)
                        .append(" to i8*\n");

                strTmp = temps.newTemp();

                llvm.append("  ").append(strTmp)
                        .append(" = call %String* @createString(i8* ")
                        .append(castTmp)
                        .append(")\n");
            }

            else {
                throw new RuntimeException(
                        "Unsupported list element type for string list: " + type);
            }

            llvm.append("  call void @arraylist_add_String(%ArrayList* ")
                    .append(listCast)
                    .append(", %String* ")
                    .append(strTmp)
                    .append(")\n");
        }

        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");

        return llvm.toString();
    }

    public String emitEmpty(Type elementType) {

        StringBuilder llvm = new StringBuilder();

        String listPtr = temps.newTemp();

        llvm.append("  ").append(listPtr)
                .append(" = call i8* @arraylist_create(i64 4)\n");

        llvm.append(";;VAL:").append(listPtr).append(";;TYPE:i8*\n");

        return llvm.toString();
    }


    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        int endIdx = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, endIdx).trim();
    }

    private String extractType(String code) {
        int idx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", idx);
        return code.substring(idx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}