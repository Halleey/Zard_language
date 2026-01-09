package memory_manager.ownership;

import ast.ASTNode;
import context.statics.Symbol;
import memory_manager.ownership.enums.OwnerShipAction;
public class OwnershipAnnotation {

    public final ASTNode node;
    public final OwnerShipAction action;
    public final Symbol from;
    public final Symbol to;

    public OwnershipAnnotation(ASTNode node,
                               OwnerShipAction action,
                               Symbol from,
                               Symbol to) {
        this.node = node;
        this.action = action;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[OWNERSHIP] " + action + " :: "
                + from.getName()
                + (to != null ? " -> " + to.getName() : "");
    }
}

