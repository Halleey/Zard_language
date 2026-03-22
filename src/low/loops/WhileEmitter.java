package low.loops;

import ast.ASTNode;
import ast.loops.WhileNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMVoid;

public class WhileEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;

    public WhileEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public LLVMValue emit(WhileNode node) {

        StringBuilder llvm = new StringBuilder();

        String condLabel = temps.newLabel("while_cond");
        String bodyLabel = temps.newLabel("while_body");
        String endLabel  = temps.newLabel("while_end");

        // jump inicial
        llvm.append("  br label %").append(condLabel).append("\n");

        // --- CONDIÇÃO ---
        llvm.append(condLabel).append(":\n");

        LLVMValue cond = node.getCondition().accept(visitor);
        llvm.append(cond.getCode());

        llvm.append("  br i1 ")
                .append(cond.getName())
                .append(", label %").append(bodyLabel)
                .append(", label %").append(endLabel)
                .append("\n");

        // --- CORPO ---
        llvm.append(bodyLabel).append(":\n");

        visitor.getControlFlow().pushLoopEnd(endLabel);

        for (ASTNode stmt : node.getBody()) {
            LLVMValue v = stmt.accept(visitor);
            llvm.append(v.getCode());
        }

        visitor.getControlFlow().popLoopEnd();

        llvm.append("  br label %").append(condLabel).append("\n");

        // --- END ---
        llvm.append(endLabel).append(":\n");

        return new LLVMValue(
                new LLVMVoid(),
                "",
                llvm.toString()
        );
    }
}
