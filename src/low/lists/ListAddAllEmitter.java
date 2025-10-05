package low.lists;

import ast.ASTNode;
import ast.lists.ListAddAllNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.LLVMEmitVisitor;


public class ListAddAllEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStringManager;

    public ListAddAllEmitter(TempManager temps, GlobalStringManager globalStringManager) {
        this.temps = temps;
        this.globalStringManager = globalStringManager;
    }

    public String emit(ListAddAllNode node, LLVMEmitVisitor visitor) {
        StringBuilder llvm = new StringBuilder();

        // LLVM do target list
        ASTNode targetListNode = node.getTargetListNode();
        String listCode = targetListNode.accept(visitor);
        llvm.append(listCode);
        String listTmp = extractTemp(listCode);
        String listCastTmp = temps.newTemp();
        llvm.append("  ").append(listCastTmp)
                .append(" = bitcast i8* ").append(listTmp)
                .append(" to %ArrayList*\n");

        int n = node.getArgs().size();
        if (n == 0) return llvm.toString(); // nada pra adicionar

        // detecta tipo do primeiro elemento (supõe tipo homogêneo)
        ASTNode first = node.getArgs().get(0);
        String firstCode = first.accept(visitor);
        llvm.append(firstCode);
        String firstType = extractType(firstCode);

        switch (firstType) {
                case "i32" -> {
                    // cria array temporário de ints
                    String tmpArray = temps.newTemp();
                    llvm.append("  ").append(tmpArray)
                            .append(" = alloca i32, i64 ").append(n).append("\n");

                    for (int i = 0; i < n; i++) {
                        ASTNode valueNode = node.getArgs().get(i);
                        String valCode = valueNode.accept(visitor);
                        llvm.append(valCode);
                        String valTmp = extractTemp(valCode);

                        String gepTmp = temps.newTemp();
                        llvm.append("  ").append(gepTmp)
                                .append(" = getelementptr inbounds i32, i32* ")
                                .append(tmpArray).append(", i64 ").append(i).append("\n");
                        llvm.append("  store i32 ").append(valTmp)
                                .append(", i32* ").append(gepTmp).append("\n");
                    }

                    llvm.append("  call void @arraylist_addAll_int(%ArrayList* ")
                            .append(listCastTmp).append(", i32* ")
                            .append(tmpArray).append(", i64 ").append(n).append(")\n");
                }

                case "double" -> {
                    // cria array temporário de doubles
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
                            .append(listCastTmp).append(", double* ")
                            .append(tmpArray).append(", i64 ").append(n).append(")\n");
                }
            case "%String*" -> { // variáveis String
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
                            .append(" = getelementptr inbounds %String*, %String** ").append(tmpArray)
                            .append(", i64 ").append(i).append("\n");
                    llvm.append("  store %String* ").append(valTmp).append(", %String** ").append(gepTmp).append("\n");
                }

                llvm.append("  call void @arraylist_addAll_String(%ArrayList* ")
                        .append(listCastTmp)
                        .append(", %String** ").append(tmpArray)
                        .append(", i64 ").append(n).append(")\n");
            }

            case "i8*" -> { // literais char*
                // aloca array temporário de i8* para addAll
                String tmpArray = temps.newTemp();
                llvm.append("  ").append(tmpArray)
                        .append(" = alloca i8*, i64 ").append(n).append("\n");

                for (int i = 0; i < n; i++) {
                    ASTNode valueNode = node.getArgs().get(i);
                    String valTmp;

                    if (valueNode instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                        // literal i8* → bitcast para i8*
                        String literal = (String) lit.value.getValue();
                        String strName = globalStringManager.getOrCreateString(literal);
                        valTmp = temps.newTemp(); // temp único para cada literal
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

                    // getelementptr no array temporário
                    String gepTmp = temps.newTemp();
                    llvm.append("  ").append(gepTmp)
                            .append(" = getelementptr inbounds i8*, i8** ")
                            .append(tmpArray)
                            .append(", i64 ").append(i).append("\n");

                    llvm.append("  store i8* ").append(valTmp).append(", i8** ").append(gepTmp).append("\n");
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
