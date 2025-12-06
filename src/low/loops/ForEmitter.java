package low.loops;

import ast.ASTNode;
import ast.loops.ForNode;
import low.TempManager;
import low.module.LLVisitorMain;

public class ForEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;

    public ForEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public String emit(ForNode node) {
        StringBuilder llvm = new StringBuilder();

        String initLabel = temps.newLabel("for_init");
        String condLabel = temps.newLabel("for_cond");
        String bodyLabel = temps.newLabel("for_body");
        String incLabel  = temps.newLabel("for_inc");
        String endLabel  = temps.newLabel("for_end");

        llvm.append("  br label %").append(initLabel).append("\n");

        llvm.append(initLabel).append(":\n");
        if (node.getInit() != null) {
            llvm.append(node.getInit().accept(visitor));
        }
        llvm.append("  br label %").append(condLabel).append("\n");

        llvm.append(condLabel).append(":\n");
        if (node.getCondition() != null) {
            String condCode = node.getCondition().accept(visitor);
            String condVal = extractTemp(condCode);
            llvm.append(condCode);
            llvm.append("  br i1 ").append(condVal)
                    .append(", label %").append(bodyLabel)
                    .append(", label %").append(endLabel).append("\n");
        } else {
            llvm.append("  br label %").append(bodyLabel).append("\n");
        }

        llvm.append(bodyLabel).append(":\n");
        visitor.getControlFlow().pushLoopEnd(endLabel);

        for (ASTNode stmt : node.getBody()) {
            llvm.append(stmt.accept(visitor));
        }

        visitor.getControlFlow().popLoopEnd();
        llvm.append("  br label %").append(incLabel).append("\n");

        llvm.append(incLabel).append(":\n");
        if (node.getIncrement() != null) {
            llvm.append(node.getIncrement().accept(visitor));
        }
        llvm.append("  br label %").append(condLabel).append("\n");

        llvm.append(endLabel).append(":\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1)
            throw new RuntimeException("Não encontrou ;;VAL: em:\n" + code);

        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
