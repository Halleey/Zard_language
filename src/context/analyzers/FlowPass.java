package context.analyzers;

import ast.ASTNode;
import ast.functions.FunctionNode;

import java.util.List;

public class FlowPass {

    private final FlowAnalyzer analyzer = new FlowAnalyzer();

    public void analyze(List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            visit(node);
        }
    }

    private void visit(ASTNode node) {

        if (node instanceof FunctionNode fn) {
            analyzer.analyzeFunction(fn);
        }

        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
    }
}
