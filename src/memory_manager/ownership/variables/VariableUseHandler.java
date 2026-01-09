package memory_manager.ownership.variables;

import ast.ASTNode;
import ast.variables.VariableNode;
import context.statics.Symbol;
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
                       Map<Symbol, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {

        Symbol sym = var.getStaticContext().resolveVariable(var.getName());
        if (sym == null) return;

        VarOwnerShip ownership = vars.get(sym);
        if (ownership == null) return;

        // Use-after-move
        if (ownership.getState() == OwnershipState.MOVED) {
            throw new RuntimeException(
                    "Use-after-move detected: " + sym.getName()
            );
        }

        // BORROW annotation
        annotations.add(
                new OwnershipAnnotation(
                        var,
                        OwnerShipAction.BORROW,
                        sym,
                        null
                )
        );

        if (debug) {
            System.out.println("[OWNERSHIP] BORROW use " + sym.getName());
        }
    }
}
