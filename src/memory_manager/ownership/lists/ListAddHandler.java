package memory_manager.ownership.lists;

import ast.ASTNode;
import ast.lists.ListAddNode;
import ast.variables.VariableNode;
import context.statics.Symbol;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.VarOwnerShip;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.utils.OwnershipUtils;
import memory_manager.ownership.variables.NodeHandler;

import java.util.List;
import java.util.Map;


public class ListAddHandler implements NodeHandler<ListAddNode> {

    @Override
    public boolean canHandle(ASTNode node) {
        return node instanceof ListAddNode;
    }

    @Override
    public void handle(ListAddNode add,
                       Map<Symbol, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {

        if (!(add.getValuesNode() instanceof VariableNode var)) return;

        Symbol source = var.getStaticContext().resolveVariable(var.getName());
        if (source == null) return;

        Symbol target = OwnershipUtils.resolveListTargetSymbol(add.getListNode());
        if (target == null) return;

        VarOwnerShip v = vars.get(source);
        if (v == null) return;

        if (v.getState() == OwnershipState.MOVED) {
            throw new RuntimeException("Use-after-move in list add: " + source.getName());
        }

        v.setState(OwnershipState.MOVED);
        annotations.add(new OwnershipAnnotation(add, OwnerShipAction.MOVED, source, target));
        graph.move(source, target);

        if (debug) {
            System.out.println("[OWNERSHIP] MOVE " + source.getName() + " -> LIST " + target.getName());
        }
    }
}
