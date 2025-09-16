package home;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

import java.util.List;
import java.util.Map;

import java.util.List;

public class MainAST extends ASTNode {
    public final List<ASTNode> body;

    public MainAST(List<ASTNode> body) {
        this.body = body;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue result = null;
        for (ASTNode node : body) {
            result = node.evaluate(ctx);
        }
        return result; // retorna o último valor ou null
    }
}
