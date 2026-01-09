package memory_manager.ownership.variables;

import ast.ASTNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableNode;
import context.statics.StaticContext;
import context.statics.Symbol;
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
    public void handle(
            AssignmentNode assign,
            Map<Symbol, VarOwnerShip> vars,
            OwnershipGraph graph,
            List<OwnershipAnnotation> annotations,
            boolean debug
    ) {

        if (!(assign.getValueNode() instanceof VariableNode rhs)) return;

        StaticContext ctx = assign.getStaticContext();

        Symbol lhsSym = ctx.resolveVariable(assign.getName());
        Symbol rhsSym = ctx.resolveVariable(rhs.getName());

        if (lhsSym == null || rhsSym == null) return;

        VarOwnerShip rhsOwnership = vars.get(rhsSym);

        if (vars.containsKey(lhsSym) && rhsOwnership != null) {

            if (rhsOwnership.getState() == OwnershipState.MOVED) {
                throw new RuntimeException(
                        "Copy from moved value: " + rhsSym.getName()
                );
            }

            VarOwnerShip newOwnership = new VarOwnerShip(lhsSym);
            vars.put(lhsSym, newOwnership);

            annotations.add(
                    new OwnershipAnnotation(
                            assign,
                            OwnerShipAction.DEEP_COPY,
                            rhsSym,
                            lhsSym
                    )
            );

            graph.deepCopy(rhsSym, lhsSym);

            if (debug) {
                System.out.println("[OWNERSHIP] DEEP_COPY "
                        + rhsSym.getName() + " -> " + lhsSym.getName());
            }
            return;
        }

        if (rhsOwnership != null) {
            rhsOwnership.setState(OwnershipState.MOVED);
        }

        vars.put(lhsSym, new VarOwnerShip(lhsSym));

        annotations.add(
                new OwnershipAnnotation(
                        assign,
                        OwnerShipAction.MOVED,
                        rhsSym,
                        lhsSym
                )
        );

        graph.move(rhsSym, lhsSym);

        if (debug) {
            System.out.println("[OWNERSHIP] MOVE "
                    + rhsSym.getName() + " -> " + lhsSym.getName());
        }
    }
}
