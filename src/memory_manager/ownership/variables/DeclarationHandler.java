package memory_manager.ownership.variables;

import ast.ASTNode;
import ast.lists.ListNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Symbol;
import context.statics.symbols.Type;
import context.statics.symbols.UnknownType;
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

        Type type = decl.getType();

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
            System.out.println("[OWNERSHIP] declare "
                    + symbol.getName() + " => OWNED");
        }

        ASTNode init = decl.getInitializer();

        if (init instanceof ListNode listNode) {

            for (ASTNode element : listNode.getChildren()) {

                if (element instanceof VariableNode varNode) {

                    Symbol source = varNode.getSymbol();
                    if (source == null) continue;

                    VarOwnerShip sourceOwnership = vars.get(source);
                    if (sourceOwnership == null) continue;

                    if (sourceOwnership.getState() == OwnershipState.MOVED) {
                        throw new RuntimeException(
                                "Use-after-move in list initializer: "
                                        + source.getName());
                    }

                    sourceOwnership.setState(OwnershipState.MOVED);

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

    private boolean isPrimitive(Type type) {

        if (type instanceof PrimitiveTypes) return true;

        if (type instanceof UnknownType) return true;

        return false;
    }
}