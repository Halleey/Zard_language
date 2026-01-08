package low.ifs;

import ast.ASTNode;
import ast.exceptions.BreakNode;
import ast.ifstatements.IfNode;
import low.TempManager;
import low.module.LLVisitorMain;


public class IfEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private int labelCounter = 0;

    public IfEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public String emit(IfNode node) {
        StringBuilder llvm = new StringBuilder();

        String condCode = node.condition.accept(visitor);
        String condTemp = extractVal(condCode);
        llvm.append(condCode).append("\n");

        String thenLabel = "then_" + labelCounter;
        String elseLabel = "else_" + labelCounter;
        String endLabel = "endif_" + labelCounter;
        labelCounter++;

        llvm.append("  br i1 ").append(condTemp)
                .append(", label %").append(thenLabel)
                .append(", label %").append(node.elseBranch != null ? elseLabel : endLabel).append("\n");

        llvm.append(thenLabel).append(":\n");
        visitor.getVariableEmitter().enterScope();
        for (ASTNode stmt : node.thenBranch) {
            llvm.append(stmt.accept(visitor));
        }
        visitor.getVariableEmitter().exitScope();
        llvm.append("  br label %").append(endLabel).append("\n");

        if (node.elseBranch != null) {
            llvm.append(elseLabel).append(":\n");
            visitor.getVariableEmitter().enterScope();
            for (ASTNode stmt : node.elseBranch) {
                llvm.append(stmt.accept(visitor));
            }
            visitor.getVariableEmitter().exitScope();
            llvm.append("  br label %").append(endLabel).append("\n");
        }

        llvm.append(endLabel).append(":\n");

        return llvm.toString();
    }


    private String extractVal(String code) {
        int idxVal = code.lastIndexOf(";;VAL:");
        if (idxVal == -1) throw new RuntimeException("Não encontrou ;;VAL: em:\n" + code);
        int idxType = code.indexOf(";;TYPE:", idxVal);
        return code.substring(idxVal + 6, idxType).trim();
    }
}
