package translate;

import ast.ASTNode;
import ast.context.StaticContext;
import ast.context.statics.ScopeKind;

import java.util.List;
public class StaticBinder {

    public void bind(List<ASTNode> nodes) {
        StaticContext root = new StaticContext(ScopeKind.GLOBAL);

        for (ASTNode node : nodes) {
            node.bind(root);
        }

        System.out.println("========");
        root.debugPrint(" ");
        System.out.println("========");
    }
}
