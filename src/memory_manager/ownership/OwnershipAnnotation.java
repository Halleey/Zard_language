package memory_manager.ownership;

import ast.ASTNode;
import memory_manager.ownership.enums.OwnerShipAction;

public class OwnershipAnnotation {

    public final ASTNode node;
    public final OwnerShipAction action;
    public final String from;
    public final String to;

    public OwnershipAnnotation(ASTNode node,
                               OwnerShipAction action,
                               String from,
                               String to) {
        this.node = node;
        this.action = action;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[OWNERSHIP] " + action + " :: " + from + (to != null ? " -> " + to : "");
    }
}
