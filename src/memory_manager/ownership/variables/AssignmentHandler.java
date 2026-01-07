package memory_manager.ownership.variables;

import ast.ASTNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableNode;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.VarOwnerShip;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.graphs.OwnershipGraph;

import java.util.List;
import java.util.Map;

public class AssignmentHandler implements NodeHandler<AssignmentNode> {

    @Override
    public boolean canHandle(ASTNode node) {
        return node instanceof AssignmentNode;
    }

    @Override
    public void handle(AssignmentNode assign,
                       Map<String, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {

        if (!(assign.getValueNode() instanceof VariableNode rhs)) return;

        String lhs = assign.getName();
        String rhsName = rhs.getName();
        VarOwnerShip rhsVar = vars.get(rhsName);

        if (vars.containsKey(lhs) && rhsVar != null) {
            if (rhsVar.state == OwnershipState.MOVED) {
                throw new RuntimeException("Copy from moved value: " + rhsName);
            }

            vars.put(lhs, new VarOwnerShip(lhs));
            annotations.add(new OwnershipAnnotation(assign, OwnerShipAction.DEEP_COPY, rhsName, lhs));
            graph.deepCopy(rhsName, lhs);

            if (debug) System.out.println("[OWNERSHIP] DEEP_COPY " + rhsName + " -> " + lhs);
            return;
        }

        if (rhsVar != null) rhsVar.state = OwnershipState.MOVED;
        vars.put(lhs, new VarOwnerShip(lhs));
        annotations.add(new OwnershipAnnotation(assign, OwnerShipAction.MOVED, rhsName, lhs));
        graph.move(rhsName, lhs);

        if (debug) System.out.println("[OWNERSHIP] MOVE " + rhsName + " -> " + lhs);
    }
}
