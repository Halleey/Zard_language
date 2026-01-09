package memory_manager.free;

import ast.ASTNode;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import context.statics.ScopeKind;
import context.statics.StaticContext;
import context.statics.Symbol;
import memory_manager.ownership.OwnershipAnnotation;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.graphs.OwnershipNode;

import java.util.*;
import java.util.*;


public class FreePlanner {

    private final OwnershipGraph ownership;
    private final Map<Symbol, ASTNode> lastUseNode;
    private final List<OwnershipAnnotation> annotations;

    private final Set<Symbol> alreadyPlanned = new HashSet<>();

    public FreePlanner(
            OwnershipGraph ownership,
            Map<Symbol, ASTNode> lastUseNode,
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
        Symbol sym = root.getSymbol();

        if (alreadyPlanned.contains(sym)) return;
        alreadyPlanned.add(sym);

        ASTNode anchor = lastUseNode.get(sym);
        if (anchor == null) {
            System.out.println("[FREE PLANNER] " + sym.getName() + " não tem uso, ignora.");
            return;
        }

        StaticContext declCtx = sym.getDeclaredIn();
        StaticContext useCtx = anchor.getStaticContext();

        boolean isRootOrGlobal =
                declCtx.getKind() == ScopeKind.ROOT ||
                        declCtx.getKind() == ScopeKind.GLOBAL;

        ASTNode freeAnchor = anchor;

        if (isRootOrGlobal) {
            // sobe até o nó que engloba o último uso
            while (freeAnchor.getParent() != null &&
                    (freeAnchor.getParent() instanceof IfNode
                            || freeAnchor.getParent() instanceof WhileNode)) {
                freeAnchor = freeAnchor.getParent();
            }
        } else {
            if (useCtx.hasLifetimeBoundary() && useCtx != declCtx) {
                System.out.println("[FREE PLANNER] " + sym.getName()
                        + " cruza boundary de lifetime, não libera.");
                return;
            }

            freeAnchor = findBlockEndForDeclaration(declCtx, anchor);
        }

        result.computeIfAbsent(freeAnchor, k -> new ArrayList<>())
                .add(new FreeAction(freeAnchor, root));

        System.out.println("[FREE PLANNER] Planejando free de "
                + sym.getName() + " após "
                + freeAnchor.getClass().getSimpleName());
    }

    /**
     * Retorna o último nó do bloco onde a variável foi declarada
     */
    private ASTNode findBlockEndForDeclaration(StaticContext declCtx, ASTNode anchor) {
        ASTNode block = anchor;

        while (block != null && block.getStaticContext() != declCtx) {
            block = block.getParent();
        }

        if (block == null) return anchor;

        List<ASTNode> children = block.getChildren();
        if (children != null && !children.isEmpty()) {
            return children.get(children.size() - 1);
        }

        return block;
    }
}
