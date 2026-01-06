package memory_manager.free;

import ast.ASTNode;
import memory_manager.ownership.graphs.OwnershipNode;


public final class FreeAction {

    private final ASTNode anchor;
    private final OwnershipNode root;

    public FreeAction(ASTNode anchor, OwnershipNode root) {
        this.anchor = anchor;
        this.root = root;
    }

    public ASTNode getAnchor() {
        return anchor;
    }

    public OwnershipNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return "free(" + root.getId() + ") after " + anchor.getClass().getSimpleName();
    }
}
