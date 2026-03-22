package low.lists.string;

import ast.ASTNode;
import ast.lists.ListNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMString;

import java.util.List;

public class ListStringEmitter {

    private final TempManager temps;

    public ListStringEmitter(TempManager temps) {
        this.temps = temps;
    }

        public LLVMValue emit(ListNode node, LLVisitorMain visitor) {

            List<ASTNode> elements = node.getList().getElements();
            int n = elements.size();

            StringBuilder llvm = new StringBuilder();

            String rawPtr = temps.newTemp();
            llvm.append("  ").append(rawPtr)
                    .append(" = call i8* @arraylist_create(i64 ")
                    .append(Math.max(4, n))
                    .append(")\n");

            String listPtr = temps.newTemp();
            llvm.append("  ").append(listPtr)
                    .append(" = bitcast i8* ").append(rawPtr)
                    .append(" to %ArrayListString*\n");

            LLVMArrayList listType = new LLVMArrayList(new LLVMString());

            for (ASTNode element : elements) {
                LLVMValue elemVal = element.accept(visitor);
                llvm.append(elemVal.getCode());

                String strTmp;

                LLVMTYPES type = elemVal.getType();
                String name = elemVal.getName();

                if (type.toString().equals("%String*")) {
                    strTmp = name;
                } else if (type.toString().equals("i8*")) {
                    strTmp = temps.newTemp();
                    llvm.append("  ").append(strTmp)
                            .append(" = call %String* @createString(i8* ")
                            .append(name)
                            .append(")\n");
                } else if (type.toString().matches("\\[\\d+ x i8\\]\\*")) {
                    String castTmp = temps.newTemp();
                    llvm.append("  ").append(castTmp)
                            .append(" = bitcast ")
                            .append(type)
                            .append(" ")
                            .append(name)
                            .append(" to i8*\n");

                    strTmp = temps.newTemp();
                    llvm.append("  ").append(strTmp)
                            .append(" = call %String* @createString(i8* ")
                            .append(castTmp)
                            .append(")\n");
                } else {
                    throw new RuntimeException(
                            "Invalid element for List<string>: " + type
                    );
                }

                llvm.append("  call void @arraylist_string_add(%ArrayListString* ")
                        .append(listPtr)
                        .append(", %String* ")
                        .append(strTmp)
                        .append(")\n");
            }

            return new LLVMValue(listType, listPtr, llvm.toString());
        }
    }
