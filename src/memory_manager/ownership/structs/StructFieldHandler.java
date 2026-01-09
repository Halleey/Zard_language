package memory_manager.ownership.structs;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;
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


public class StructFieldHandler implements NodeHandler<StructFieldAccessNode> {

    @Override
    public boolean canHandle(ASTNode node) {
        return node instanceof StructFieldAccessNode;
    }

    @Override
    public void handle(StructFieldAccessNode sfa,
                       Map<Symbol, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {

        if (!(sfa.getValue() instanceof VariableNode var)) return;

        Symbol source = var.getStaticContext().resolveVariable(var.getName());
        if (source == null) return;

        Symbol target = OwnershipUtils.resolveStructFieldTargetSymbol(sfa);
        if (target == null) return;

        VarOwnerShip v = vars.get(source);
        if (v == null) return;

        if (v.getState() == OwnershipState.MOVED) {
            throw new RuntimeException("Use-after-move in struct field assignment: " + source.getName());
        }

        v.setState(OwnershipState.MOVED);
        annotations.add(new OwnershipAnnotation(sfa, OwnerShipAction.MOVED, source, target));
        graph.move(source, target);

        if (debug) {
            System.out.println("[OWNERSHIP] MOVE " + source.getName() + " -> " + target.getName());
        }
    }
}

