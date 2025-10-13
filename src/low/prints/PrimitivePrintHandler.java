package low.prints;

import ast.ASTNode;
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
            String type = visitor.getVarType(varNode.getName());
            return type.equals("i32") || type.equals("double") || type.equals("i1");
        }
        return false;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor) {
        VariableNode varNode = (VariableNode) node;
        String varName = varNode.getName();
        String type = visitor.getVarType(varName);
        String tmp = temps.newTemp();
        StringBuilder sb = new StringBuilder();

        sb.append("  ").append(tmp)
                .append(" = load ").append(type).append(", ").append(type).append("* %").append(varName).append("\n");

        switch (type) {
            case "i32" -> sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(tmp).append(")\n");
            case "double" -> sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ").append(tmp).append(")\n");
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
                .append("  call i32 (i8*, ...) @printf(i8* getelementptr ([6 x i8], [6 x i8]* @.strTrue, i32 0, i32 0))\n")
                .append("  br label %").append(endLabel).append("\n");

        sb.append(falseLabel).append(":\n")
                .append("  call i32 (i8*, ...) @printf(i8* getelementptr ([7 x i8], [7 x i8]* @.strFalse, i32 0, i32 0))\n")
                .append("  br label %").append(endLabel).append("\n");

        sb.append(endLabel).append(":\n");
    }
}
