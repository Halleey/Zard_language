package translate.front;

import ast.ASTNode;
import ast.runtime.RuntimeContext;

import java.util.List;
public class ASTInterpreter {

    public void run(List<ASTNode> ast) {
        RuntimeContext ctx = new RuntimeContext();

        for (ASTNode node : ast) {
            try {
                node.evaluate(ctx);
            } catch (Exception e) {
                System.out.println("ERRO ao executar node: " + node.getClass().getSimpleName());
                e.printStackTrace();
                throw e;
            }
        }
    }
}
