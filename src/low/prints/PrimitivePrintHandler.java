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
            return "i32".equals(llvmType) || "double".equals(llvmType) || "i1".equals(llvmType);
        }
        return false;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor) {
        VariableNode varNode = (VariableNode) node;
        String varName = varNode.getName();
        TypeInfos info = visitor.getVarType(varName);
        if (info == null) throw new RuntimeException("Variável não registrada: " + varName);

        String llvmType = info.getLLVMType();
        String tmp = temps.newTemp();
        StringBuilder sb = new StringBuilder();

        sb.append("  ").append(tmp)
                .append(" = load ").append(llvmType)
                .append(", ").append(llvmType).append("* %").append(varName).append("\n");

        switch (llvmType) {
            case "i32" -> sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), ")
                    .append("i32 ").append(tmp).append(")\n");
            case "double" -> sb.append("  call i32 (i8*, ...) @printf(")
                    .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), ")
                    .append("double ").append(tmp).append(")\n");
            case "i1" -> emitBoolPrint(sb, tmp);
        }
        return sb.toString();
    }

    void emitBoolPrint(StringBuilder sb, String tmp) {
        String trueLabel = temps.newLabel("bool_true");
        String falseLabel = temps.newLabel("bool_false");
        String endLabel = temps.newLabel("bool_end");

        sb.append("  br i1 ").append(tmp)
                .append(", label %").append(trueLabel)
                .append(", label %").append(falseLabel).append("\n");

        sb.append(trueLabel).append(":\n")
                .append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([6 x i8], [6 x i8]* @.strTrue, i32 0, i32 0))\n")
                .append("  br label %").append(endLabel).append("\n");

        sb.append(falseLabel).append(":\n")
                .append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([7 x i8], [7 x i8]* @.strFalse, i32 0, i32 0))\n")
                .append("  br label %").append(endLabel).append("\n");

        sb.append(endLabel).append(":\n");
    }
}
