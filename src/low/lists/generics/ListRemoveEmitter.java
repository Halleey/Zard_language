package low.lists.generics;

import ast.lists.ListRemoveNode;
import low.TempManager;
import low.lists.bool.ListBoolRemoveEmitter;
import low.lists.doubles.ListDoubleRemoveEmitter;
import low.lists.ints.ListRemoveIntEmitter;
import low.module.LLVMEmitVisitor;
public class ListRemoveEmitter {
    private final TempManager temps;
    private final ListRemoveIntEmitter intEmitter; // emitter específico para inteiros
    private final ListDoubleRemoveEmitter doubleRemoveEmitter;
    private final ListBoolRemoveEmitter boolRemoveEmitter;

    public ListRemoveEmitter(TempManager temps) {
        this.temps = temps;
        this.intEmitter = new ListRemoveIntEmitter(temps);
        this.doubleRemoveEmitter = new ListDoubleRemoveEmitter(temps);
        this.boolRemoveEmitter = new ListBoolRemoveEmitter(temps);
    }

    public String emit(ListRemoveNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        String listCode = node.getListNode().accept(visitor);
        String type = extractType(listCode);
        String listVal = extractValue(listCode);

        System.out.println("[DEBUG-REMOVE] Código gerado da lista:");
        System.out.println(listCode);
        System.out.println("[DEBUG-REMOVE] Tipo detectado da lista: " + type);
        System.out.println("[DEBUG-REMOVE] Valor detectado da lista: " + listVal);

        if (type.contains("ArrayListInt")) {
            return intEmitter.emit(node, visitor);
        }
        if (type.contains("ArrayListDouble")) {
            return doubleRemoveEmitter.emit(node, visitor);
        }
        if (type.contains("ArrayListBool")) {
            return boolRemoveEmitter.emit(node, visitor);
        }

        // gera código do índice
        String posCode = node.getIndexNode().accept(visitor);
        llvm.append(listCode);
        llvm.append(posCode);
        String posVal = extractValue(posCode);

        System.out.println("[DEBUG-REMOVE] Código gerado do índice:");
        System.out.println(posCode);
        System.out.println("[DEBUG-REMOVE] Valor detectado do índice: " + posVal);

        // converte índice para i64
        String posCast = temps.newTemp();
        llvm.append("  ").append(posCast)
                .append(" = sext i32 ").append(posVal).append(" to i64\n");


        // lista interna de struct -> já temos %ArrayList*
        // lista "solta" -> pode vir como i8* e precisa bitcast
        if (type.contains("ArrayList")) {
            System.out.println("[DEBUG-REMOVE] Lista já é %ArrayList*, chamando removeItem direto.");
            llvm.append("  call void @removeItem(%ArrayList* ")
                    .append(listVal).append(", i64 ").append(posCast).append(")\n");
        } else {
            System.out.println("[DEBUG-REMOVE] Lista veio como i8*, aplicando bitcast para %ArrayList*.");
            String listCast = temps.newTemp();
            llvm.append("  ").append(listCast)
                    .append(" = bitcast i8* ").append(listVal)
                    .append(" to %ArrayList*\n");
            llvm.append("  call void @removeItem(%ArrayList* ")
                    .append(listCast).append(", i64 ").append(posCast).append(")\n");
        }

        return llvm.toString();
    }
    private String extractValue(String code) {
        String last = null;
        for (String line : code.split("\n")) {
            if (line.contains(";;VAL:")) {
                String val = line.split(";;VAL:")[1].trim();
                if (val.contains(";;TYPE")) {
                    val = val.split(";;TYPE")[0].trim();
                }
                last = val;
            }
        }
        return last;
    }

    private String extractType(String code) {
        String last = null;
        for (String line : code.split("\n")) {
            if (line.contains(";;TYPE:")) {
                last = line.split(";;TYPE:")[1].trim();
            }
        }
        return last;
    }

}
