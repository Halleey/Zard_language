package low.whiles;

import ast.ASTNode;
import ast.loops.WhileNode;
import low.TempManager;
import low.module.LLVisitorMain;

public class WhileEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;

    public WhileEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public String emit(WhileNode node) {
        StringBuilder llvm = new StringBuilder();

        String condLabel = temps.newLabel("while_cond");
        String bodyLabel = temps.newLabel("while_body");
        String endLabel  = temps.newLabel("while_end");

        llvm.append("  br label %").append(condLabel).append("\n");

        // --- condição ---
        llvm.append(condLabel).append(":\n");
        String condCode = node.condition.accept(visitor);
        String condVal = extractTemp(condCode);
        llvm.append(condCode);
        llvm.append("  br i1 ").append(condVal)
                .append(", label %").append(bodyLabel)
                .append(", label %").append(endLabel).append("\n");

        // --- corpo ---
        llvm.append(bodyLabel).append(":\n");
        visitor.pushLoopEnd(endLabel);

        for (ASTNode stmt : node.body) {
            llvm.append(stmt.accept(visitor)); // já gera store/load com ponteiro correto
        }

        visitor.popLoopEnd();
        llvm.append("  br label %").append(condLabel).append("\n");
        llvm.append(endLabel).append(":\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
