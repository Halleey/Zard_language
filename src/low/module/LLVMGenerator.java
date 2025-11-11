package low.module;

import ast.ASTNode;
import ast.TypeSpecializer;

import java.util.List;


public class LLVMGenerator {
    private final LLVisitorMain visitor;

    public LLVMGenerator(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public LLVisitorMain getVisitor() {
        return visitor;
    }

    public String generate(List<ASTNode> ast) {
        StringBuilder llvm = new StringBuilder();
        for (ASTNode node : ast) {
            String code = node.accept(visitor);
            if (code != null && !code.isBlank()) {
                llvm.append(code);
            }
        }

        return llvm.toString();
    }

}
