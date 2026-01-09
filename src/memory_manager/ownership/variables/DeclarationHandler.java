package memory_manager.ownership.variables;

import ast.ASTNode;
import ast.variables.VariableDeclarationNode;
import context.statics.Symbol;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.VarOwnerShip;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.graphs.OwnershipGraph;

import java.util.List;
import java.util.Map;

public class DeclarationHandler implements NodeHandler<VariableDeclarationNode> {

    @Override
    public boolean canHandle(ASTNode node) {
        return node instanceof VariableDeclarationNode;
    }

    @Override
    public void handle(VariableDeclarationNode decl,
                       Map<Symbol, VarOwnerShip> vars,
                       OwnershipGraph graph,
                       List<OwnershipAnnotation> annotations,
                       boolean debug) {

        String type = decl.getType();
        if (type == null || isPrimitive(type)) {
            if (debug) {
                System.out.println("[OWNERSHIP] declare "
                        + decl.getName() + " => PRIMITIVE, ignorado");
            }
            return;
        }

        Symbol symbol = decl.getSymbol();
        if (symbol == null) {
            throw new IllegalStateException(
                    "VariableDeclarationNode sem Symbol: " + decl.getName()
            );
        }

        // Ownership state
        VarOwnerShip ownership = new VarOwnerShip(symbol);
        vars.put(symbol, ownership);

        // Ownership graph
        graph.declareVar(symbol);

        // Annotation
        annotations.add(
                new OwnershipAnnotation(
                        decl,
                        OwnerShipAction.OWNED,
                        symbol,
                        null
                )
        );

        if (debug) {
            System.out.println("[OWNERSHIP] declare "
                    + symbol.getName() + " => OWNED");
        }
    }

    private boolean isPrimitive(String type) {
        type = type.trim().toLowerCase();
        return type.equals("int")
                || type.equals("float")
                || type.equals("double")
                || type.equals("bool")
                || type.equals("char");
    }
}
