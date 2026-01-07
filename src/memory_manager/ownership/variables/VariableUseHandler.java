package memory_manager.ownership.variables;

import ast.ASTNode;
import ast.variables.VariableNode;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.VarOwnerShip;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.graphs.OwnershipGraph;

import java.util.List;
import java.util.Map;

public class VariableUseHandler implements NodeHandler<VariableNode> {

    @Override
    public boolean canHandle(ASTNode node) {
        return node instanceof VariableNode;
    }

    @Override
    public void handle(VariableNode var,
                       Map<String, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {
        VarOwnerShip v = vars.get(var.getName());
        if (v == null) return;
        if (v.state == OwnershipState.MOVED) {
            throw new RuntimeException("Use-after-move detected: " + var.getName());
        }

        annotations.add(new OwnershipAnnotation(var, OwnerShipAction.BORROW, var.getName(), null));
        if (debug) System.out.println("[OWNERSHIP] BORROW use " + var.getName());
    }
}
