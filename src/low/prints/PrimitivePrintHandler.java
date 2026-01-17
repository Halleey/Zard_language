package low.prints;

import ast.ASTNode;
import low.main.TypeInfos;
import low.module.LLVisitorMain;


import ast.variables.VariableNode;
import low.TempManager;
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

        return switch (info.getLLVMType()) {
            case "i32","i8", "double", "float", "i1" -> true;
            default -> false;
        };
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor, boolean newline) {

        VariableNode var = (VariableNode) node;
        TypeInfos info = visitor.getVarType(var.getName());
        String llvmType = info.getLLVMType();

        String ptr = visitor.varEmitter.getVarPtr(var.getName());
        if (ptr == null)
            throw new RuntimeException("Ponteiro LLVM não encontrado para " + var.getName());

        String valTmp = temps.newTemp();
        StringBuilder sb = new StringBuilder();

        sb.append("  ")
                .append(valTmp)
                .append(" = load ")
                .append(llvmType)
                .append(", ")
                .append(llvmType)
                .append("* ")
                .append(ptr)
                .append("\n")
                .append(";;VAL:")
                .append(valTmp)
                .append(";;TYPE:")
                .append(llvmType)
                .append("\n");

        switch (llvmType) {

            case "i32" ->
                    appendPrintf(sb, valTmp, newline, ".strInt", "i32");

            case "double" ->
                    appendPrintf(sb, valTmp, newline, ".strDouble", "double");

            case "float" -> {
                String ext = temps.newTemp();
                sb.append("  ")
                        .append(ext)
                        .append(" = fpext float ")
                        .append(valTmp)
                        .append(" to double\n")
                        .append(";;VAL:")
                        .append(ext)
                        .append(";;TYPE:double\n");

                appendPrintf(sb, ext, newline, ".strFloat", "double");
            }

            case "i8" -> {
                String ext = temps.newTemp();
                sb.append("  ")
                        .append(ext)
                        .append(" = zext i8 ")
                        .append(valTmp)
                        .append(" to i32\n")
                        .append(";;VAL:")
                        .append(ext)
                        .append(";;TYPE:i32\n");

                appendPrintf(sb, ext, newline, ".strChar", "i32");
            }


            case "i1" ->
                    emitBoolPrint(sb, valTmp, newline);

            default ->
                    throw new RuntimeException("Tipo primitivo não suportado: " + llvmType);
        }

        return sb.toString();
    }

    private void appendPrintf(StringBuilder sb, String tmp,
                              boolean newline, String strLabel,
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
                .append(", label %")
                .append(trueLabel)
                .append(", label %")
                .append(falseLabel)
                .append("\n");

        sb.append(trueLabel).append(":\n")
                .append("  call i32 (i8*, ...) @printf(i8* getelementptr ([6 x i8], [6 x i8]* ")
                .append(trueStr)
                .append(", i32 0, i32 0))\n")
                .append("  br label %")
                .append(endLabel)
                .append("\n");

        sb.append(falseLabel).append(":\n")
                .append("  call i32 (i8*, ...) @printf(i8* getelementptr ([7 x i8], [7 x i8]* ")
                .append(falseStr)
                .append(", i32 0, i32 0))\n")
                .append("  br label %")
                .append(endLabel)
                .append("\n");

        sb.append(endLabel).append(":\n");
    }
}
