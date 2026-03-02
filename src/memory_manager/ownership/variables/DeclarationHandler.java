package memory_manager.ownership.variables;

import ast.ASTNode;
import ast.lists.ListNode;
import ast.variables.VariableDeclarationNode;
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

        VarOwnerShip ownership = new VarOwnerShip(symbol);
        vars.put(symbol, ownership);
        graph.declareVar(symbol);

        annotations.add(
                new OwnershipAnnotation(
                        decl,
                        OwnerShipAction.OWNED,
                        symbol,
                        null
                )
        );

        if (debug) {
            System.out.println("[OWNERSHIP] declare " + symbol.getName() + " => OWNED");
        }

        ASTNode init = decl.getInitializer();

        if (init instanceof ListNode listNode) {

            for (ASTNode element : listNode.getChildren()) {

                if (element instanceof VariableNode varNode) {

                    Symbol source = varNode.getStaticContext()
                            .resolveVariable(varNode.getName());

                    if (source == null) continue;

                    VarOwnerShip sourceOwnership = vars.get(source);
                    if (sourceOwnership == null) continue;

                    if (sourceOwnership.getState() == OwnershipState.MOVED) {
                        throw new RuntimeException(
                                "Use-after-move in list initializer: "
                                        + source.getName());
                    }

                    // 🔥 MOVE LINEAR
                    sourceOwnership.setState(OwnershipState.MOVED);

                    // 🔥 MOVE NO GRAFO
                    graph.moveIntoList(source, symbol);

                    annotations.add(
                            new OwnershipAnnotation(
                                    decl,
                                    OwnerShipAction.MOVED,
                                    source,
                                    symbol
                            )
                    );

                    if (debug) {
                        System.out.println("[OWNERSHIP] MOVE "
                                + source.getName()
                                + " -> LIST "
                                + symbol.getName());
                    }
                }
            }
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
