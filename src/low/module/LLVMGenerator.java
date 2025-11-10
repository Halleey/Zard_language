package low.module;

import ast.ASTNode;
import ast.TypeSpecializer;

import java.util.List;

public class LLVMGenerator {
    private final LLVisitorMain visitor;

    public LLVMGenerator(TypeSpecializer typeSpecializer) {
        this.visitor = new LLVisitorMain(typeSpecializer);
    }

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
