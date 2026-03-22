package low.loops;

import ast.ASTNode;
import ast.loops.ForNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMVoid;

public class ForEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;

    public ForEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public LLVMValue emit(ForNode node) {
        StringBuilder llvm = new StringBuilder();

        String initLabel = temps.newLabel("for_init");
        String condLabel = temps.newLabel("for_cond");
        String bodyLabel = temps.newLabel("for_body");
        String incLabel  = temps.newLabel("for_inc");
        String endLabel  = temps.newLabel("for_end");

        llvm.append("  br label %").append(initLabel).append("\n");

        // INIT
        llvm.append(initLabel).append(":\n");
        if (node.getInit() != null) {
            LLVMValue init = node.getInit().accept(visitor);
            llvm.append(init.getCode());
        }
        llvm.append("  br label %").append(condLabel).append("\n");

        // CONDITION
        llvm.append(condLabel).append(":\n");
        if (node.getCondition() != null) {
            LLVMValue cond = node.getCondition().accept(visitor);

            llvm.append(cond.getCode());

            llvm.append("  br i1 ").append(cond.getName())
                    .append(", label %").append(bodyLabel)
                    .append(", label %").append(endLabel).append("\n");
        } else {
            llvm.append("  br label %").append(bodyLabel).append("\n");
        }

        // BODY
        llvm.append(bodyLabel).append(":\n");
        visitor.getControlFlow().pushLoopEnd(endLabel);

        for (ASTNode stmt : node.getBody()) {
            LLVMValue stmtVal = stmt.accept(visitor);
            llvm.append(stmtVal.getCode());
        }

        visitor.getControlFlow().popLoopEnd();
        llvm.append("  br label %").append(incLabel).append("\n");

        // INCREMENT
        llvm.append(incLabel).append(":\n");
        if (node.getIncrement() != null) {
            LLVMValue inc = node.getIncrement().accept(visitor);
            llvm.append(inc.getCode());
        }
        llvm.append("  br label %").append(condLabel).append("\n");

        // END
        llvm.append(endLabel).append(":\n");

        return new LLVMValue(
                new LLVMVoid(),
                "",
                llvm.toString()
        );
    }
}
