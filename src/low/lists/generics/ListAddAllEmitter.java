package low.lists.generics;

import ast.ASTNode;
import ast.lists.ListAddAllNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.lists.ints.ListIntAddAllEmitter;
import low.main.GlobalStringManager;
import low.module.LLVMEmitVisitor;


public class ListAddAllEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStringManager;
    private final ListIntAddAllEmitter intAddAllEmitter;

    public ListAddAllEmitter(TempManager temps, GlobalStringManager globalStringManager) {
        this.temps = temps;
        this.globalStringManager = globalStringManager;
        this.intAddAllEmitter = new ListIntAddAllEmitter(temps);
    }

    public String emit(ListAddAllNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        ASTNode targetListNode = node.getTargetListNode();
        String listCode = targetListNode.accept(visitor);
        llvm.append(listCode);
        String listTmp = extractTemp(listCode);

        int n = node.getArgs().size();
        if (n == 0) return llvm.toString();

        ASTNode first = node.getArgs().get(0);
        String firstCode = first.accept(visitor);
        llvm.append(firstCode);
        String firstType = extractType(firstCode);

        if (firstType.equals("i32")) {
            return intAddAllEmitter.emit(node, visitor);
        }

        String listCastTmp = temps.newTemp();
        llvm.append("  ").append(listCastTmp)
                .append(" = bitcast i8* ").append(listTmp)
                .append(" to %ArrayList*\n");

        switch (firstType) {
            case "double" -> {
                String tmpArray = temps.newTemp();
                llvm.append("  ").append(tmpArray)
                        .append(" = alloca double, i64 ").append(n).append("\n");

                for (int i = 0; i < n; i++) {
                    ASTNode valueNode = node.getArgs().get(i);
                    String valCode = valueNode.accept(visitor);
                    llvm.append(valCode);
                    String valTmp = extractTemp(valCode);

                    String gepTmp = temps.newTemp();
                    llvm.append("  ").append(gepTmp)
                            .append(" = getelementptr inbounds double, double* ")
                            .append(tmpArray).append(", i64 ").append(i).append("\n");
                    llvm.append("  store double ").append(valTmp)
                            .append(", double* ").append(gepTmp).append("\n");
                }

                llvm.append("  call void @arraylist_addAll_double(%ArrayList* ")
                        .append(listCastTmp)
                        .append(", double* ").append(tmpArray)
                        .append(", i64 ").append(n).append(")\n");
            }

            case "%String*" -> {
                String tmpArray = temps.newTemp();
                llvm.append("  ").append(tmpArray)
                        .append(" = alloca %String*, i64 ").append(n).append("\n");

                for (int i = 0; i < n; i++) {
                    ASTNode valueNode = node.getArgs().get(i);
                    String valCode = valueNode.accept(visitor);
                    llvm.append(valCode);
                    String valTmp = extractTemp(valCode);

                    String gepTmp = temps.newTemp();
                    llvm.append("  ").append(gepTmp)
                            .append(" = getelementptr inbounds %String*, %String** ")
                            .append(tmpArray).append(", i64 ").append(i).append("\n");
                    llvm.append("  store %String* ").append(valTmp)
                            .append(", %String** ").append(gepTmp).append("\n");
                }

                llvm.append("  call void @arraylist_addAll_String(%ArrayList* ")
                        .append(listCastTmp)
                        .append(", %String** ").append(tmpArray)
                        .append(", i64 ").append(n).append(")\n");
            }

            case "i8*" -> {
                String tmpArray = temps.newTemp();
                llvm.append("  ").append(tmpArray)
                        .append(" = alloca i8*, i64 ").append(n).append("\n");

                for (int i = 0; i < n; i++) {
                    ASTNode valueNode = node.getArgs().get(i);
                    String valTmp;

                    if (valueNode instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                        String literal = (String) lit.value.getValue();
                        String strName = globalStringManager.getOrCreateString(literal);
                        valTmp = temps.newTemp();
                        llvm.append("  ").append(valTmp)
                                .append(" = bitcast [")
                                .append(literal.length() + 1)
                                .append(" x i8]* ").append(strName)
                                .append(" to i8*\n");
                    } else {
                        String valCode = valueNode.accept(visitor);
                        llvm.append(valCode);
                        valTmp = extractTemp(valCode);
                    }

                    String gepTmp = temps.newTemp();
                    llvm.append("  ").append(gepTmp)
                            .append(" = getelementptr inbounds i8*, i8** ")
                            .append(tmpArray)
                            .append(", i64 ").append(i).append("\n");

                    llvm.append("  store i8* ").append(valTmp)
                            .append(", i8** ").append(gepTmp).append("\n");
                }

                llvm.append("  call void @arraylist_addAll_string(%ArrayList* ")
                        .append(listCastTmp)
                        .append(", i8** ").append(tmpArray)
                        .append(", i64 ").append(n).append(")\n");
            }

            default -> throw new RuntimeException("Unsupported element type in ListAddAll: " + firstType);
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
