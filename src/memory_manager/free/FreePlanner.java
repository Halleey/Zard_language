package memory_manager.free;

import ast.ASTNode;
import context.statics.StaticContext;
import context.statics.Symbol;
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
    private final List<OwnershipAnnotation> annotations;

    private final Set<String> alreadyPlanned = new HashSet<>();

    public FreePlanner(
            OwnershipGraph ownership,
            Map<String, ASTNode> lastUseNode,
            List<OwnershipAnnotation> annotations
    ) {
        this.ownership = ownership;
        this.lastUseNode = lastUseNode;
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

        if (alreadyPlanned.contains(var)) return;
        alreadyPlanned.add(var);

        // não libera se a variável foi MOVED
        if (wasMoved(var)) {
            System.out.println("[FREE PLANNER] " + var + " foi movida, não libera.");
            return;
        }

        ASTNode anchor = lastUseNode.get(var);
        if (anchor == null) {
            System.out.println("[FREE PLANNER] " + var + " não tem uso, ignora.");
            return;
        }

        Symbol sym = anchor.getStaticContext().resolveVariable(var);
        StaticContext declCtx = sym.getDeclaredIn();
        StaticContext useCtx = anchor.getStaticContext();


        // se a variável escapa para um escopo maior que o de declaração, não libera
        if (!declCtx.isAncestorOf(useCtx)) {
            System.out.println("[FREE PLANNER] " + var + " escapa para escopo maior, não libera.");
            return;
        }

        // se o contexto do uso tem boundary de lifetime mas não é o contexto de declaração, não libera
        if (useCtx.hasLifetimeBoundary() && useCtx != declCtx) {
            System.out.println("[FREE PLANNER] " + var + " está em boundary de lifetime, não libera.");
            return;
        }

        // se passou nos testes, pode liberar
        result.computeIfAbsent(anchor, k -> new ArrayList<>())
                .add(new FreeAction(anchor, root));

        System.out.println("[FREE PLANNER] Planejando free para " + var + " após " + anchor.getClass().getSimpleName());

        // planeja recursivamente para filhos
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
