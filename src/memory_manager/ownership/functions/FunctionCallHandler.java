package memory_manager.ownership.functions;


import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.variables.VariableNode;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.VarOwnerShip;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.variables.NodeHandler;

import java.util.List;
import java.util.Map;

public class FunctionCallHandler implements NodeHandler<FunctionCallNode> {

    @Override
    public boolean canHandle(ASTNode node) {
        return node instanceof FunctionCallNode;
    }

    @Override
    public void handle(FunctionCallNode call,
                       Map<String, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {
        for (ASTNode arg : call.getArgs()) {
            if (arg instanceof VariableNode var) {
                VarOwnerShip v = vars.get(var.getName());
                if (v != null) {
                    annotations.add(new OwnershipAnnotation(arg, OwnerShipAction.BORROW, var.getName(), null));
                    if (debug) System.out.println("[OWNERSHIP] BORROW argumento " + var.getName() + " na chamada de função");
                }
            }
        }
    }
}
