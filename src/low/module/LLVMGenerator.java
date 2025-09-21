package low.module;

import ast.ASTNode;

import java.util.List;

public class LLVMGenerator {
    private final LLVisitorMain visitor = new LLVisitorMain();

    public LLVisitorMain getVisitor() {
        return visitor;
    }

    public String generate(List<ASTNode> ast) {
        StringBuilder llvm = new StringBuilder();
        for (ASTNode node : ast) {
            llvm.append(node.accept(visitor));
        }
        return llvm.toString();
    }
}
