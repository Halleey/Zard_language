package memory_manager.ownership.utils;

import ast.ASTNode;
import ast.prints.PrintNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.VariableNode;
import context.statics.Symbol;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.VarOwnerShip;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.structs.StructFieldHandler;
import memory_manager.ownership.variables.NodeHandler;

import java.util.List;
import java.util.Map;
public class PrintNodeHandler implements NodeHandler<PrintNode> {

    @Override
    public boolean canHandle(ASTNode node) {
        return node instanceof PrintNode;
    }

    @Override
    public void handle(PrintNode printNode,
                       Map<Symbol, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {

        checkNode(printNode.expr, vars, graph, annotations, debug);
    }

    // Método recursivo para checar uso de variáveis ou structs
    private void checkNode(ASTNode node,
                           Map<Symbol, VarOwnerShip> vars,
                           OwnershipGraph graph,
                           List<OwnershipAnnotation> annotations,
                           boolean debug) {

        if (node instanceof VariableNode varNode) {
            Symbol sym = varNode.getStaticContext().resolveVariable(varNode.getName());

            VarOwnerShip v = vars.get(sym);
            if (v == null) return;

            if (v.getState() == OwnershipState.MOVED) {
                throw new RuntimeException("Use-after-move in print(): " + sym.getName());
            }

            // Marca como lido
            annotations.add(new OwnershipAnnotation(node, OwnerShipAction.READ, sym, null));

            if (debug) {
                System.out.println("[OWNERSHIP] READ/PRINT " + sym.getName());
            }

        } else if (node instanceof StructFieldAccessNode sfa) {
            // Reutiliza o StructFieldHandler
            new StructFieldHandler().handle(sfa, vars, graph, annotations, debug);

        } else if (node instanceof PrintNode pn) {
            checkNode(pn.expr, vars, graph, annotations, debug);

        } else {
            for (ASTNode child : node.getChildren()) {
                checkNode(child, vars, graph, annotations, debug);
            }
        }
    }
}
