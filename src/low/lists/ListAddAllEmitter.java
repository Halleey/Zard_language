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

            ASTNode targetListNode = node.getTargetListNode();
            String listTmp;
            String listCastTmp;


            String listCode = targetListNode.accept(visitor);
            llvm.append(listCode);
            listTmp = extractTemp(listCode);
            listCastTmp = temps.newTemp();
            llvm.append("  ").append(listCastTmp)
                    .append(" = bitcast i8* ").append(listTmp)
                    .append(" to %ArrayList*\n");

            for (ASTNode valueNode : node.getArgs()) {
                String valCode = valueNode.accept(visitor);
                llvm.append(valCode);

                String valTmp = extractTemp(valCode);
                String valType = extractType(valCode);

                switch (valType) {
                    case "i32" -> llvm.append("  call void @arraylist_add_int(%ArrayList* ")
                            .append(listCastTmp).append(", i32 ").append(valTmp).append(")\n");
                    case "double" -> llvm.append("  call void @arraylist_add_double(%ArrayList* ")
                            .append(listCastTmp).append(", double ").append(valTmp).append(")\n");
                    case "i8*" -> {
                        if (valueNode instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                            // literal de string
                            String literal = (String) lit.value.getValue();
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
                    case "%String" -> llvm.append("  call void @arraylist_add_String(%ArrayList* ")
                            .append(listCastTmp).append(", %String* ").append(valTmp).append(")\n");
                    default -> throw new RuntimeException("Unsupported element type in ListAddAll: " + valType);
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
