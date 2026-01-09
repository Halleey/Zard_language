package memory_manager.free;

import ast.ASTNode;
import memory_manager.ownership.graphs.OwnershipNode;

public record FreeAction(ASTNode anchor, OwnershipNode root) {

    @Override
    public String toString() {
        return "free(" +
                root.getSymbol().getName() +
                ") after " +
                anchor.getClass().getSimpleName();
    }
}
