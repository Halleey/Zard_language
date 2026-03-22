package low.prints;

import ast.ASTNode;
import ast.lists.ListGetNode;
import ast.variables.VariableNode;
import context.statics.symbols.*;
import low.TempManager;
import low.lists.generics.ListGetEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;

public class ListGetPrintHandler implements PrintHandler {

    private final TempManager temps;
    private final ListGetEmitter listGetEmitter;

    public ListGetPrintHandler(TempManager temps, ListGetEmitter listGetEmitter) {
        this.temps = temps;
        this.listGetEmitter = listGetEmitter;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        return node instanceof ListGetNode;
    }

    @Override
    public LLVMValue emit(ASTNode node, LLVisitorMain visitor, boolean newline) {

        ListGetNode getNode = (ListGetNode) node;

        LLVMValue val = listGetEmitter.emit(getNode, visitor);

        StringBuilder llvm = new StringBuilder();
        llvm.append(val.getCode());

        String labelSuffix = newline ? "" : "_noNL";

        LLVMTYPES type = val.getType();

        if (type instanceof LLVMInt) {

            llvm.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt")
                    .append(labelSuffix)
                    .append(", i32 0, i32 0), i32 ")
                    .append(val.getName())
                    .append(")\n");
        }

        else if (type instanceof LLVMDouble || type instanceof LLVMFloat) {

            llvm.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble")
                    .append(labelSuffix)
                    .append(", i32 0, i32 0), double ")
                    .append(val.getName())
                    .append(")\n");
        }

        else if (type instanceof LLVMString) {

            llvm.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strStr")
                    .append(labelSuffix)
                    .append(", i32 0, i32 0), i8* ")
                    .append(val.getName())
                    .append(")\n");
        }

        else if (type instanceof LLVMBool) {

            String id = temps.newTemp().replace("%", "");

            String labelTrue = "bool_true_" + id;
            String labelFalse = "bool_false_" + id;
            String labelEnd = "bool_end_" + id;

            llvm.append("  br i1 ").append(val.getName())
                    .append(", label %").append(labelTrue)
                    .append(", label %").append(labelFalse).append("\n");

            llvm.append(labelTrue).append(":\n");

            llvm.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([6 x i8], [6 x i8]* @.strTrue")
                    .append(labelSuffix)
                    .append(", i32 0, i32 0))\n");

            llvm.append("  br label %").append(labelEnd).append("\n");

            llvm.append(labelFalse).append(":\n");

            llvm.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([7 x i8], [7 x i8]* @.strFalse")
                    .append(labelSuffix)
                    .append(", i32 0, i32 0))\n");

            llvm.append("  br label %").append(labelEnd).append("\n");

            llvm.append(labelEnd).append(":\n");
        }

        else if (type instanceof LLVMStruct struct) {

            String structName = normalizeStructName(struct.getName());

            llvm.append("  call void @print_")
                    .append(structName)
                    .append("(i8* ")
                    .append(val.getName())
                    .append(")\n");
        }

        else if (type instanceof LLVMArrayList) {

            llvm.append("  ; nested list printing not implemented\n");
        }

        else {
            llvm.append("  ; unknown type: ").append(type).append("\n");
        }

        return new LLVMValue(type, val.getName(), llvm.toString());
    }

    private String normalizeStructName(String name) {
        return name.replace('.', '_');
    }
}