package memory_manager.borrows;

import ast.ASTNode;

public class OwnerShipInfo {

    public OwnershipState state;
    public ASTNode owner;
    public ASTNode origin;
    public AssignKind transferKind;
    public int depth;
    public boolean isDeepCopy = false; // novo campo

    public OwnerShipInfo(OwnershipState state, ASTNode owner, ASTNode origin,
                         AssignKind transferKind, int depth) {
        this.state = state;
        this.owner = owner;
        this.origin = origin;
        this.transferKind = transferKind;
        this.depth = depth;
    }

    public OwnerShipInfo deepCopy(ASTNode newOwner) {
        OwnerShipInfo copy = new OwnerShipInfo(
                OwnershipState.OWNED,
                newOwner,
                this.origin,
                AssignKind.ORIGEM,
                this.depth
        );
        copy.isDeepCopy = true;
        return copy;
    }
}
