package memory_manager.ownership.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.variables.VariableNode;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.VarOwnerShip;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.variables.NodeHandler;

import java.util.List;
import java.util.Map;

public class ReturnHandler implements NodeHandler<ReturnNode> {

    @Override
    public boolean canHandle(ASTNode node) {
        return node instanceof ReturnNode;
    }

    @Override
    public void handle(ReturnNode ret,
                       Map<String, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {

        if (!(ret.getExpr() instanceof VariableNode var)) return;

        VarOwnerShip v = vars.get(var.getName());
        if (v == null) return;

        v.state = OwnershipState.MOVED;
        annotations.add(new OwnershipAnnotation(ret, OwnerShipAction.MOVED, var.getName(), "return"));
        graph.move(var.getName(), "return");

        if (debug) System.out.println("[OWNERSHIP] MOVE via return: " + var.getName());
    }
}
