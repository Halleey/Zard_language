package low.prints;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.lists.ListGetNode;
import ast.lists.ListNode;
import ast.lists.ListSizeNode;
import ast.prints.PrintNode;
import low.TempManager;
import low.lists.generics.ListGetEmitter;
import low.lists.generics.ListSizeEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;
public class ExprPrintHandler {

    private final TempManager temps;

    public ExprPrintHandler(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emitExprOrElement(LLVMValue val,
                                       LLVisitorMain visitor,
                                       ASTNode node,
                                       boolean newline) {

        StringBuilder llvm = new StringBuilder();
        llvm.append(val.getCode());

        LLVMTYPES type = val.getType();
        String temp = val.getName();

        if (type instanceof LLVMInt) {
            appendPrintf(llvm, temp, newline, ".strInt", "i32");
        }

        else if (type instanceof LLVMDouble) {
            appendPrintf(llvm, temp, newline, ".strDouble", "double");
        }

        else if (type instanceof LLVMFloat) {
            String tmpExt = temps.newTemp();

            llvm.append("  ").append(tmpExt)
                    .append(" = fpext float ").append(temp).append(" to double\n");

            appendPrintf(llvm, tmpExt, newline, ".strFloat", "double");
        }

        else if (type instanceof LLVMBool) {
            new PrimitivePrintHandler(temps)
                    .emitBoolPrint(llvm, temp, newline);
        }

        else if (type instanceof LLVMChar) {
            String castTmp = temps.newTemp();

            llvm.append("  ").append(castTmp)
                    .append(" = sext i8 ").append(temp).append(" to i32\n");

            appendPrintf(llvm, castTmp, newline, ".strChar", "i32");
        }

        else if (type instanceof LLVMString) {
            String fn = newline ? "@printString" : "@printString_noNL";

            llvm.append("  call void ").append(fn)
                    .append("(%String* ").append(temp).append(")\n");
        }

        else if (type instanceof LLVMArrayList) {
            return new ListPrintHandler(temps)
                    .emit(node, visitor, newline);
        }

        else if (type instanceof LLVMStruct) {
            return new StructPrintHandler(temps)
                    .emit(node, visitor, newline);
        }

        else {
            throw new RuntimeException(
                    "Unsupported type in print: " + type);
        }

        return new LLVMValue(new LLVMVoid(), "void", llvm.toString());
    }

    private void appendPrintf(StringBuilder llvm,
                              String temp,
                              boolean newline,
                              String strLabel,
                              String llvmType) {

        String label = newline ? strLabel : strLabel + "_noNL";

        llvm.append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([")
                .append(newline ? "4 x i8" : "3 x i8")
                .append("], [")
                .append(newline ? "4 x i8" : "3 x i8")
                .append("]* @").append(label)
                .append(", i32 0, i32 0), ")
                .append(llvmType).append(" ").append(temp).append(")\n");
    }
}