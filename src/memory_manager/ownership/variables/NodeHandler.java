package memory_manager.ownership.variables;

import ast.ASTNode;
import context.statics.Symbol;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.VarOwnerShip;
import memory_manager.ownership.graphs.OwnershipGraph;

import java.util.List;
import java.util.Map;

public interface NodeHandler<T extends ASTNode> {

    boolean canHandle(ASTNode node);

    void handle(
            T node,
            Map<Symbol, VarOwnerShip> vars,
            OwnershipGraph graph,
            List<OwnershipAnnotation> annotations,
            boolean debug
    );
}
