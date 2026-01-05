package memory_manager.ownership.frees;

import memory_manager.ownership.graphs.OwnershipNode;

public final class FreeAction {

    private final OwnershipNode root;

    public FreeAction(OwnershipNode root) {
        this.root = root;
    }

    public OwnershipNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return "free(" + root.getId() + ")";
    }
}

