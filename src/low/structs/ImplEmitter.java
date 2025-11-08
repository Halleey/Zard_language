package low.structs;

import ast.functions.FunctionNode;
import ast.structs.ImplNode;
import low.module.LLVisitorMain;

public class ImplEmitter {
    private final LLVisitorMain visitor;

    public ImplEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public String emit(ImplNode node) {
        StringBuilder llvm = new StringBuilder();
        String structName = node.getStructName();

        llvm.append("; === Impl para Struct<").append(structName).append("> ===\n");

        for (FunctionNode fn : node.getMethods()) {
            llvm.append(fn.accept(visitor));
        }

        llvm.append("\n");
        return llvm.toString();
    }
}
