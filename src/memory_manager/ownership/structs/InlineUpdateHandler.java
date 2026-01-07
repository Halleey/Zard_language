package memory_manager.ownership.structs;

import ast.ASTNode;
import ast.structs.StructUpdateNode;
import ast.variables.VariableNode;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.VarOwnerShip;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.variables.NodeHandler;

import java.util.List;
import java.util.Map;

public class InlineUpdateHandler implements NodeHandler<StructUpdateNode> {

    @Override
    public boolean canHandle(ASTNode node) {
        return node instanceof StructUpdateNode;
    }

    @Override
    public void handle(StructUpdateNode up,
                       Map<String, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {

        if (!(up.getTargetStruct() instanceof VariableNode var)) return;

        VarOwnerShip v = vars.get(var.getName());
        if (v == null) return;
        if (v.state == OwnershipState.MOVED)
            throw new RuntimeException("Inline update on moved value: " + var.getName());

        if (debug) System.out.println("[OWNERSHIP] INLINE UPDATE consumes exclusive ownership of " + var.getName());
    }
}
