package translate.front;

import ast.ASTNode;
import ast.runtime.RuntimeContext;

import java.util.List;

public class ASTInterpreter {

    public void run(List<ASTNode> ast) {
        RuntimeContext ctx = new RuntimeContext();
        for (ASTNode node : ast) {
            node.evaluate(ctx);
        }
    }
}
