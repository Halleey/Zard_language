package low.ifs;

import ast.ASTNode;
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

        //  avalia a condição
        String condCode = node.condition.accept(visitor);
        String condTemp = extractVal(condCode);
        llvm.append(condCode).append("\n");

        // cria labels únicos
        String thenLabel = "then_" + labelCounter;
        String elseLabel = "else_" + labelCounter;
        String endLabel = "endif_" + labelCounter;
        labelCounter++;

        //  branch condicional
        llvm.append("  br i1 ").append(condTemp)
                .append(", label %").append(thenLabel)
                .append(", label %").append(node.elseBranch != null ? elseLabel : endLabel).append("\n");

        // then block
        llvm.append(thenLabel).append(":\n");
        for (ASTNode stmt : node.thenBranch) {
            llvm.append(stmt.accept(visitor));
        }
        llvm.append("  br label %").append(endLabel).append("\n");

        //  else block (se existir)
        if (node.elseBranch != null) {
            llvm.append(elseLabel).append(":\n");
            for (ASTNode stmt : node.elseBranch) {
                llvm.append(stmt.accept(visitor));
            }
            llvm.append("  br label %").append(endLabel).append("\n");
        }

        // end label
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