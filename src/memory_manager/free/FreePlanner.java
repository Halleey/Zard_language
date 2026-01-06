package memory_manager.free;

import ast.ASTNode;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.escapes.EscapeInfo;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.graphs.OwnershipNode;

import java.util.*;
import java.util.*;

public class FreePlanner {

    private final OwnershipGraph ownership;
    private final Map<String, ASTNode> lastUseNode;
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

        // Ignora se a variável escapou
        if (escapeInfo != null && escapeInfo.escapes(var)) {
            return;
        }

        // Ignora se a variável foi movida
        if (wasMoved(var)) {
            return;
        }

        // Obtém o nó de último uso
        ASTNode anchor = lastUseNode.get(var);
        if (anchor == null) {
            // Se não houver uso, podemos liberar logo na declaração ou ignorar
            return;
        }

        // Adiciona FreeAction imediatamente após o último uso
        result
                .computeIfAbsent(anchor, k -> new ArrayList<>())
                .add(new FreeAction(anchor, root));

        // Planeja recursivamente para filhos (campos de structs / listas internas)
        for (OwnershipNode child : root.getChildren()) {
            planRoot(child, result);
        }
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