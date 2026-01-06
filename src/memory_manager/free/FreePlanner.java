package memory_manager.free;

import ast.ASTNode;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.escapes.EscapeInfo;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.graphs.OwnershipNode;

import java.util.*;

public class FreePlanner {

    private final OwnershipGraph ownership;
    private final Map<String,   ASTNode> lastUseNode;
    private final EscapeInfo escapeInfo;
    private final List<OwnershipAnnotation> annotations;

    private final Set<String> alreadyPlanned = new HashSet<>();

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

        for (OwnershipNode root : ownership.getRoots().values()) {
            planRoot(root, result);
        }

        return result;
    }

    private void planRoot(OwnershipNode root, Map<ASTNode, List<FreeAction>> result) {

        String var = root.getId();

        if (alreadyPlanned.contains(var)) {
            return;
        }

        alreadyPlanned.add(var);

        if (escapeInfo != null && escapeInfo.escapes(var)) {
            return;
        }

        if (wasMoved(var)) {
            return;
        }

        ASTNode anchor = lastUseNode.get(var);
        if (anchor == null) {
            return;
        }

        result
                .computeIfAbsent(anchor, k -> new ArrayList<>())
                .add(new FreeAction(anchor, root));
    }

    private boolean wasMoved(String var) {
        for (OwnershipAnnotation ann : annotations) {
            if (ann.action == OwnerShipAction.MOVED && var.equals(ann.from)) {
                return true;
            }
        }
        return false;
    }
}
