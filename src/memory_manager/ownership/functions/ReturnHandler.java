package memory_manager.ownership.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.variables.VariableNode;
import context.statics.Symbol;
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
                       Map<Symbol, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {

        if (!(ret.getExpr() instanceof VariableNode var)) return;

        Symbol sym = var.getStaticContext().resolveVariable(var.getName());
        if (sym == null) return;

        VarOwnerShip v = vars.get(sym);
        if (v == null) return;

        v.setState(OwnershipState.MOVED);

        Symbol returnSym = sym.rebased("return");

        annotations.add(
                new OwnershipAnnotation(
                        ret,
                        OwnerShipAction.MOVED,
                        sym,
                        returnSym
                )
        );

        graph.move(sym, returnSym);

        if (debug) {
            System.out.println("[OWNERSHIP] MOVE via return: " + sym.getName());
        }
    }
}
