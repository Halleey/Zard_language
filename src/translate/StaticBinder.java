package translate;

import ast.ASTNode;
import context.statics.StaticContext;
import context.statics.ScopeKind;

import java.util.List;

public final class StaticBinder {

    private static StaticContext rootContext;

    public void bind(List<ASTNode> ast) {
        rootContext = new StaticContext(ScopeKind.ROOT);
        for (ASTNode node : ast) {
            node.bind(rootContext);
        }
    }

    public static StaticContext getRootContext() {
        return rootContext;
    }
}
