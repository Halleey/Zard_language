package memory_manager.free;

import ast.ASTNode;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import context.statics.ScopeKind;
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

        ASTNode anchor = lastUseNode.get(var);
        if (anchor == null) {
            System.out.println("[FREE PLANNER] " + var + " não tem uso, ignora.");
            return;
        }

        Symbol sym = anchor.getStaticContext().resolveVariable(var);
        StaticContext declCtx = sym.getDeclaredIn();
        StaticContext useCtx = anchor.getStaticContext();

        // define se é global/root
        boolean isRootOrGlobal = declCtx.getKind() == ScopeKind.ROOT
                || declCtx.getKind() == ScopeKind.GLOBAL;

        ASTNode freeAnchor = anchor;

        if (isRootOrGlobal) {
            // variáveis globais/root → sobe até bloco que engloba último uso
            while (freeAnchor.getParent() != null &&
                    (freeAnchor.getParent() instanceof IfNode
                            || freeAnchor.getParent() instanceof WhileNode)) {
                freeAnchor = freeAnchor.getParent();
            }
        } else {
            // variáveis locais → insere free **no final do bloco onde foi declarada**
            // se declCtx != useCtx e useCtx está dentro do bloco, free vai no mesmo bloco
            // se houver boundary de lifetime, ignora (como antes)
            if (useCtx.hasLifetimeBoundary() && useCtx != declCtx) {
                System.out.println("[FREE PLANNER] " + var + " está em boundary de lifetime, não libera.");
                return;
            }

            // free no final do bloco onde a variável foi declarada
            freeAnchor = findBlockEndForDeclaration(declCtx, anchor);
        }

        // Planeja o free
        result.computeIfAbsent(freeAnchor, k -> new ArrayList<>())
                .add(new FreeAction(freeAnchor, root));

        System.out.println("[FREE PLANNER] Planejando free da lista " + var +
                " após " + freeAnchor.getClass().getSimpleName());
    }

    /**
     * Procura o nó do bloco que corresponde ao final do escopo onde a variável foi declarada.
     * Para While/If, retorna o último nó do corpo do bloco.
     */
    private ASTNode findBlockEndForDeclaration(StaticContext declCtx, ASTNode anchor) {
        ASTNode block = anchor;
        // sobe até o nó que representa o escopo da declaração
        while (block != null && block.getStaticContext() != declCtx) {
            block = block.getParent();
        }

        if (block == null) {
            // fallback: ainda usamos anchor
            return anchor;
        }

        // agora block é o nó do escopo onde a variável foi declarada
        // se tiver getChildren, pegamos o último para inserir free
        List<ASTNode> children = block.getChildren();
        if (children != null && !children.isEmpty()) {
            return children.get(children.size() - 1);
        }

        return block;
    }
}