package low.prints;

import ast.ASTNode;
import low.main.TypeInfos;
import low.module.LLVisitorMain;


import ast.variables.VariableNode;
import low.TempManager;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.*;
public class PrimitivePrintHandler implements PrintHandler {

    private final TempManager temps;

    public PrimitivePrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {

        if (!(node instanceof VariableNode var)) return false;

        TypeInfos info = visitor.getVarType(var.getName());
        if (info == null) return false;

        LLVMTYPES type = info.getLLVMType();

        return type instanceof LLVMInt
                || type instanceof LLVMDouble
                || type instanceof LLVMFloat
                || type instanceof LLVMBool
                || type instanceof LLVMChar;
    }

    @Override
    public LLVMValue emit(ASTNode node, LLVisitorMain visitor, boolean newline) {

        VariableNode var = (VariableNode) node;

        LLVMValue val = visitor.varEmitter.emitLoad(var.getName());

        StringBuilder sb = new StringBuilder();
        sb.append(val.getCode());

        LLVMTYPES type = val.getType();
        String tmp = val.getName();

        if (type instanceof LLVMInt) {
            appendPrintf(sb, tmp, newline, ".strInt", "i32");
        }

        else if (type instanceof LLVMDouble) {
            appendPrintf(sb, tmp, newline, ".strDouble", "double");
        }

        else if (type instanceof LLVMFloat) {

            String ext = temps.newTemp();

            sb.append("  ").append(ext)
                    .append(" = fpext float ")
                    .append(tmp)
                    .append(" to double\n");

            appendPrintf(sb, ext, newline, ".strFloat", "double");
        }

        else if (type instanceof LLVMChar) {

            String ext = temps.newTemp();

            sb.append("  ").append(ext)
                    .append(" = zext i8 ")
                    .append(tmp)
                    .append(" to i32\n");

            appendPrintf(sb, ext, newline, ".strChar", "i32");
        }

        else if (type instanceof LLVMBool) {
            emitBoolPrint(sb, tmp, newline);
        }

        else {
            throw new RuntimeException("Tipo primitivo não suportado: " + type);
        }

        return new LLVMValue(new LLVMVoid(), "void", sb.toString());
    }

    private void appendPrintf(StringBuilder sb,
                              String tmp,
                              boolean newline,
                              String strLabel,
                              String type) {

        String label = newline ? strLabel : strLabel + "_noNL";

        sb.append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([")
                .append(newline ? "4 x i8" : "3 x i8")
                .append("], [")
                .append(newline ? "4 x i8" : "3 x i8")
                .append("]* @")
                .append(label)
                .append(", i32 0, i32 0), ")
                .append(type)
                .append(" ")
                .append(tmp)
                .append(")\n");
    }

    void emitBoolPrint(StringBuilder sb, String tmp, boolean newline) {

        String trueLabel  = temps.newLabel("bool_true");
        String falseLabel = temps.newLabel("bool_false");
        String endLabel   = temps.newLabel("bool_end");

        String trueStr  = newline ? "@.strTrue"  : "@.strTrue_noNL";
        String falseStr = newline ? "@.strFalse" : "@.strFalse_noNL";

        sb.append("  br i1 ")
                .append(tmp)
                .append(", label %").append(trueLabel)
                .append(", label %").append(falseLabel)
                .append("\n");

        sb.append(trueLabel).append(":\n")
                .append("  call i32 (i8*, ...) @printf(i8* getelementptr ([6 x i8], [6 x i8]* ")
                .append(trueStr)
                .append(", i32 0, i32 0))\n")
                .append("  br label %").append(endLabel).append("\n");

        sb.append(falseLabel).append(":\n")
                .append("  call i32 (i8*, ...) @printf(i8* getelementptr ([7 x i8], [7 x i8]* ")
                .append(falseStr)
                .append(", i32 0, i32 0))\n")
                .append("  br label %").append(endLabel).append("\n");

        sb.append(endLabel).append(":\n");
    }
}