package memory_manager.ownership;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.lists.ListAddNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstanceNode;
import ast.structs.StructUpdateNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.functions.FunctionCallHandler;
import memory_manager.ownership.functions.ReturnHandler;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.lists.ListAddHandler;
import memory_manager.ownership.structs.InlineUpdateHandler;
import memory_manager.ownership.structs.StructFieldHandler;
import memory_manager.ownership.utils.OwnershipUtils;
import memory_manager.ownership.variables.AssignmentHandler;
import memory_manager.ownership.variables.DeclarationHandler;
import memory_manager.ownership.variables.NodeHandler;
import memory_manager.ownership.variables.VariableUseHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class OwnershipAnalyzer {

    private final Map<String, VarOwnerShip> vars = new LinkedHashMap<>();
    private final List<OwnershipAnnotation> annotations = new ArrayList<>();
    private final OwnershipGraph graph = new OwnershipGraph();
    private final boolean debug;

    private final List<NodeHandler<?>> handlers = new ArrayList<>();

    public OwnershipAnalyzer(boolean debug) {
        this.debug = debug;
        handlers.add(new DeclarationHandler());
        handlers.add(new AssignmentHandler());
        handlers.add(new VariableUseHandler());
        handlers.add(new FunctionCallHandler());
        handlers.add(new StructFieldHandler());
        handlers.add(new ListAddHandler());
        handlers.add(new ReturnHandler());
        handlers.add(new InlineUpdateHandler());
    }

    public void analyzeBlock(List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            analyzeNode(node);
        }
    }

    private void analyzeNode(ASTNode node) {
        for (NodeHandler<?> handler : handlers) {
            if (handler.canHandle(node)) {
                ((NodeHandler<ASTNode>) handler).handle(node, vars, graph, annotations, debug);
                return; // se handler cuidou, não visita filhos
            }
        }

        // visita filhos se ninguém tratou
        for (ASTNode child : node.getChildren()) {
            analyzeNode(child);
        }
    }

    public List<OwnershipAnnotation> getAnnotations() { return annotations; }
    public OwnershipGraph getGraph() { return graph; }

    public void dumpFinalStates() {
        System.out.println("==== FINAL OWNERSHIP STATE (linear) ====");
        for (VarOwnerShip v : vars.values()) System.out.println(v);
        System.out.println();
        graph.dump();
    }
}
