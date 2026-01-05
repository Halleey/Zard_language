package memory_manager.free;

import ast.ASTNode;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.escapes.EscapeInfo;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.graphs.OwnershipNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class FreePlanner {

    private final OwnershipGraph ownership;
    private final Map<String, ASTNode> lastUseNode;
    private final EscapeInfo escapeInfo;
    private final List<OwnershipAnnotation> annotations;

    public FreePlanner(
            OwnershipGraph ownership,
            Map<String, ASTNode> lastUseNode,
            EscapeInfo escapeInfo,
            List<OwnershipAnnotation> annotations
    ) {
        this.ownership = ownership;
        this.lastUseNode = lastUseNode;
        this.escapeInfo = escapeInfo;
        this.annotations = annotations;
    }

    public Map<ASTNode, List<FreeAction>> plan() {
        Map<ASTNode, List<FreeAction>> result = new LinkedHashMap<>();

        // percorre cada raiz do ownership graph
        for (OwnershipNode root : ownership.getRoots().values()) {
            planNode(root, result);
        }

        return result;
    }

    private void planNode(OwnershipNode node, Map<ASTNode, List<FreeAction>> result) {
        String var = node.getId();

        // ignora variáveis que escapam
        if (escapeInfo != null && escapeInfo.escapes(var)) {
            return;
        }

        // verifica se esta variável foi MOVED
        boolean moved = false;
        for (OwnershipAnnotation ann : annotations) {
            if (var.equals(ann.from) && ann.action == OwnerShipAction.MOVED) {
                moved = true;
                break;
            }
        }

        // só libera se NÃO foi movida
        if (!moved) {
            ASTNode anchor = lastUseNode.get(var);
            if (anchor != null) {
                result.computeIfAbsent(anchor, k -> new ArrayList<>())
                        .add(new FreeAction(anchor, node));
            }
        }

        // percorre filhos (struct fields)
        for (OwnershipNode child : node.getChildren()) {
            planNode(child, result);
        }
    }
}
