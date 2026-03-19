package low.lists.generics;

import ast.ASTNode;
import ast.lists.ListAddAllNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.lists.bool.ListBoolAddAllEmitter;
import low.lists.doubles.ListAddAllDoubleEmitter;
import low.lists.ints.ListIntAddAllEmitter;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMGeneric;
import low.module.builders.primitives.LLVMString;


public class ListAddAllEmitter {

    private final TempManager temps;
    private final GlobalStringManager globalStringManager;

    private final ListIntAddAllEmitter intAddAllEmitter;
    private final ListAddAllDoubleEmitter doubleEmitter;
    private final ListBoolAddAllEmitter boolAddAllEmitter;

    public ListAddAllEmitter(TempManager temps, GlobalStringManager globalStringManager) {
        this.temps = temps;
        this.globalStringManager = globalStringManager;

        this.intAddAllEmitter = new ListIntAddAllEmitter(temps);
        this.doubleEmitter = new ListAddAllDoubleEmitter(temps);
        this.boolAddAllEmitter = new ListBoolAddAllEmitter(temps);
    }

    public LLVMValue emit(ListAddAllNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        // Lista alvo
        LLVMValue listVal = node.getTargetListNode().accept(visitor);
        llvm.append(listVal.getCode());
        String listTmp = listVal.getName();

        int n = node.getArgs().size();
        if (n == 0) return listVal;

        // Primeiro elemento para decidir tipo
        LLVMValue first = node.getArgs().get(0).accept(visitor);
        llvm.append(first.getCode());
        String firstType = first.getType().toString();

        // ===== Tipos primitivos especializados =====
        if (firstType.equals("i32")) {
            return intAddAllEmitter.emit(node, visitor);
        }
        if (firstType.equals("double")) {
            return doubleEmitter.emit(node, visitor);
        }
        if (firstType.equals("i1")) {
            return boolAddAllEmitter.emit(node, visitor);
        }

        // ===== Lista de strings =====
        if (firstType.equals("%String*")) {
            String listCast = temps.newTemp();
            llvm.append("  ").append(listCast)
                    .append(" = bitcast %ArrayList* ")
                    .append(listTmp)
                    .append(" to %ArrayListString*\n");

            String tmpArray = temps.newTemp();
            llvm.append("  ").append(tmpArray)
                    .append(" = alloca %String*, i64 ").append(n).append("\n");

            for (int i = 0; i < n; i++) {
                LLVMValue val = node.getArgs().get(i).accept(visitor);
                llvm.append(val.getCode());
                String valTmp = val.getName();

                String gepTmp = temps.newTemp();
                llvm.append("  ").append(gepTmp)
                        .append(" = getelementptr inbounds %String*, %String** ")
                        .append(tmpArray).append(", i64 ").append(i).append("\n");

                llvm.append("  store %String* ").append(valTmp)
                        .append(", %String** ").append(gepTmp).append("\n");
            }

            llvm.append("  call void @arraylist_string_addAll(%ArrayListString* ")
                    .append(listCast)
                    .append(", %String** ")
                    .append(tmpArray)
                    .append(", i64 ").append(n).append(")\n");

            return new LLVMValue(
                    new LLVMArrayList(new LLVMString()),
                    listCast,
                    llvm.toString()
            );
        }

        if (firstType.equals("i8*")) {
            String listCastTmp = temps.newTemp();
            llvm.append("  ").append(listCastTmp)
                    .append(" = bitcast i8* ")
                    .append(listTmp)
                    .append(" to %ArrayList*\n");

            String tmpArray = temps.newTemp();
            llvm.append("  ").append(tmpArray)
                    .append(" = alloca i8*, i64 ").append(n).append("\n");

            for (int i = 0; i < n; i++) {
                ASTNode valueNode = node.getArgs().get(i);
                String valTmp;

                if (valueNode instanceof LiteralNode lit &&
                        lit.value.type().equals("string")) {
                    String literal = (String) lit.value.value();
                    String strName = globalStringManager.getOrCreateString(literal);
                    valTmp = temps.newTemp();
                    llvm.append("  ").append(valTmp)
                            .append(" = bitcast [")
                            .append(literal.length() + 1)
                            .append(" x i8]* ")
                            .append(strName)
                            .append(" to i8*\n");
                } else {
                    LLVMValue val = valueNode.accept(visitor);
                    llvm.append(val.getCode());
                    valTmp = val.getName();
                }

                String gepTmp = temps.newTemp();
                llvm.append("  ").append(gepTmp)
                        .append(" = getelementptr inbounds i8*, i8** ")
                        .append(tmpArray).append(", i64 ").append(i).append("\n");

                llvm.append("  store i8* ").append(valTmp)
                        .append(", i8** ").append(gepTmp).append("\n");
            }

            llvm.append("  call void @arraylist_addAll_string(%ArrayList* ")
                    .append(listCastTmp)
                    .append(", i8** ")
                    .append(tmpArray)
                    .append(", i64 ").append(n).append(")\n");

            return new LLVMValue(
                    new LLVMArrayList(new LLVMPointer(new LLVMGeneric())),
                    listCastTmp,
                    llvm.toString()
            );
        }
        throw new RuntimeException("Unsupported element type in ListAddAll: " + firstType);
    }
}