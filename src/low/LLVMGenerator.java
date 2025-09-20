package low;

import ast.ASTNode;

import java.util.List;

public class LLVMGenerator {
    public String generate(List<ASTNode> ast) {
        LLVMEmitVisitor visitor = new LLVisitorMain();
        StringBuilder llvm = new StringBuilder();
        llvm.append("; LLVM IR mínimo do main\n");

        for (ASTNode node : ast) {
            llvm.append(node.accept(visitor));
        }

        return llvm.toString();
    }

}
