package translate;

import ast.ASTNode;
import ast.context.StaticContext;

import java.util.List;

public class StaticBinder {
    public void bind(List<ASTNode> nodes) {
        StaticContext root = new StaticContext();
        for (ASTNode node : nodes) {
            node.bind(root);
        }

        System.out.println("========");
        root.debugPrint(" ");
        System.out.println("========");

    }
}
