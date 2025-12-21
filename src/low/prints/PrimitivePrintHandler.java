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
        if (node instanceof VariableNode varNode) {
            TypeInfos info = visitor.getVarType(varNode.getName());
            if (info == null) return false;
            String llvmType = info.getLLVMType();
            return "i32".equals(llvmType) || "double".equals(llvmType) || "float".equals(llvmType) || "i1".equals(llvmType);
        }
        return false;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor, boolean newline) {
        if (!(node instanceof VariableNode varNode)) {
            throw new RuntimeException("PrimitivePrintHandler só suporta VariableNode");
        }

        String varName = varNode.getName();
        TypeInfos info = visitor.getVarType(varName);
        if (info == null) throw new RuntimeException("Variável não registrada: " + varName);

        String llvmType = info.getLLVMType();
        String tmp = temps.newTemp();
        StringBuilder sb = new StringBuilder();

        sb.append("  ").append(tmp)
                .append(" = load ").append(llvmType)
                .append(", ").append(llvmType).append("* %").append(varName).append("\n")
                .append(";;VAL:").append(tmp).append(";;TYPE:").append(llvmType).append("\n");

        switch (llvmType) {
            case "i32" -> appendPrintf(sb, tmp, newline, ".strInt", "i32");
            case "double" -> appendPrintf(sb, tmp, newline, ".strDouble", "double");
            case "float" -> {
                String extTmp = temps.newTemp();
                sb.append("  ").append(extTmp)
                        .append(" = fpext float ").append(tmp).append(" to double\n")
                        .append(";;VAL:").append(extTmp).append(";;TYPE:double\n");
                appendPrintf(sb, extTmp, newline, ".strFloat", "double");
            }
            case "i1" -> emitBoolPrint(sb, tmp, newline);
            default -> throw new RuntimeException("Tipo primitivo não suportado: " + llvmType);
        }

        return sb.toString();
    }

    private void appendPrintf(StringBuilder sb, String tmp, boolean newline, String strLabel, String type) {
        String label = newline ? strLabel : strLabel + "_noNL";
        sb.append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([")
                .append(newline ? "4 x i8" : "3 x i8")
                .append("], [")
                .append(newline ? "4 x i8" : "3 x i8")
                .append("]* @").append(label)
                .append(", i32 0, i32 0), ").append(type).append(" ").append(tmp).append(")\n");
    }

    void emitBoolPrint(StringBuilder sb, String tmp, boolean newline) {
        String trueLabel = temps.newLabel("bool_true");
        String falseLabel = temps.newLabel("bool_false");
        String endLabel = temps.newLabel("bool_end");

        String trueStr = newline ? "@.strTrue" : "@.strTrue_noNL";
        String falseStr = newline ? "@.strFalse" : "@.strFalse_noNL";

        sb.append("  br i1 ").append(tmp)
                .append(", label %").append(trueLabel)
                .append(", label %").append(falseLabel).append("\n");

        sb.append(trueLabel).append(":\n")
                .append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([6 x i8], [6 x i8]* ").append(trueStr)
                .append(", i32 0, i32 0))\n")
                .append("  br label %").append(endLabel).append("\n");

        sb.append(falseLabel).append(":\n")
                .append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([7 x i8], [7 x i8]* ").append(falseStr)
                .append(", i32 0, i32 0))\n")
                .append("  br label %").append(endLabel).append("\n");

        sb.append(endLabel).append(":\n");
    }
}
