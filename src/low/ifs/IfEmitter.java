package low.ifs;

import ast.ASTNode;
import ast.exceptions.BreakNode;
import ast.ifstatements.IfNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMVoid;


public class IfEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;

    public IfEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public LLVMValue emit(IfNode node) {

        StringBuilder llvm = new StringBuilder();

        LLVMValue cond = node.getCondition().accept(visitor);

        llvm.append(cond.getCode());

        String thenLabel = temps.newLabel("then");
        String elseLabel = temps.newLabel("else");
        String endLabel  = temps.newLabel("endif");

        llvm.append("  br i1 ")
                .append(cond.getName())
                .append(", label %").append(thenLabel)
                .append(", label %").append(node.getElseBranch() != null ? elseLabel : endLabel)
                .append("\n");

        // THEN
        llvm.append(thenLabel).append(":\n");
        visitor.getVariableEmitter().enterScope();

        for (ASTNode stmt : node.getThenBranch()) {
            LLVMValue v = stmt.accept(visitor);
            llvm.append(v.getCode());
        }

        visitor.getVariableEmitter().exitScope();
        llvm.append("  br label %").append(endLabel).append("\n");

        // ELSE
        if (node.getElseBranch() != null) {
            llvm.append(elseLabel).append(":\n");

            visitor.getVariableEmitter().enterScope();

            for (ASTNode stmt : node.getElseBranch()) {
                LLVMValue v = stmt.accept(visitor);
                llvm.append(v.getCode());
            }

            visitor.getVariableEmitter().exitScope();
            llvm.append("  br label %").append(endLabel).append("\n");
        }

        llvm.append(endLabel).append(":\n");

        return new LLVMValue(
                new LLVMVoid(),
                "",
                llvm.toString()
        );
    }
}
