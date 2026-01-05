package memory_manager.lifetime;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.ifstatements.IfNode;
import ast.lists.ListAddNode;
import ast.loops.WhileNode;
import ast.prints.PrintNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructMethodCallNode;
import ast.structs.StructUpdateNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;

import java.util.*;



public class DeterministicLifetimeAnalyzer {

    private final Map<String, String> varTypes;

    // AGORA: último uso aponta para o nó real
    private final Map<String, ASTNode> lastUseNode =
            new LinkedHashMap<>();

    private final Set<String> seen = new HashSet<>();

    public DeterministicLifetimeAnalyzer(Map<String, String> varTypes) {
        this.varTypes = varTypes;
    }

    public Map<String, ASTNode> analyzeAndReturnNode(List<ASTNode> roots) {

        List<ASTNode> linear =
                collectLinearStatements(roots);
        for (int i = linear.size() - 1; i >= 0; i--) {
            ASTNode stmt = linear.get(i);
            collectUses(stmt, stmt);
        }

        return lastUseNode;
    }

    private List<ASTNode> collectLinearStatements(List<ASTNode> roots) {
        List<ASTNode> out = new ArrayList<>();
        walk(roots, out);
        return out;
    }

    private void walk(List<ASTNode> nodes, List<ASTNode> out) {
        for (ASTNode node : nodes) {

            // apenas statements reais
            if (node.isStatement()) {
                out.add(node);
            }

            // Controle de fluxo preserva ordem semântica
            if (node instanceof IfNode ifNode) {
                walk(List.of(ifNode.getCondition()), out);
                walk(ifNode.getThenBranch(), out);
                if (ifNode.getElseBranch() != null) {
                    walk(ifNode.getElseBranch(), out);
                }
                continue;
            }

            if (node instanceof WhileNode whileNode) {
                walk(List.of(whileNode.getCondition()), out);
                walk(whileNode.getBody(), out);
                continue;
            }

            if (node instanceof FunctionNode fn) {
                walk(fn.getBody(), out);
                continue;
            }

            // fallback
            walk(node.getChildren(), out);
        }
    }

    private void collectUses(ASTNode node, ASTNode anchor) {

        if (node instanceof VariableNode v) {
            recordOwner(v.getName(), anchor);
            return;
        }

        if (node instanceof ListAddNode listAdd) {
            collectUses(listAdd.getValuesNode(), anchor); // registra o valor adicionado
            collectUses(listAdd.getListNode(), anchor);   // registra a lista
            return;
        }


        if (node instanceof PrintNode print) {
            collectUses(print.expr, print);
            return;
        }

        if (node instanceof AssignmentNode a){
            collectUses(a.getValueNode(), anchor);
        return;
    }
        if (node instanceof StructFieldAccessNode f) {
            String owner = rootOwner(f.getStructInstance());
            recordOwner(owner, anchor);
        }

        if (node instanceof StructMethodCallNode m) {
            String owner = rootOwner(m.getStructInstance());
            recordOwner(owner, anchor);
        }

        if (node instanceof StructUpdateNode u) {
            String owner = rootOwner(u.getTargetStruct());
            recordOwner(owner, anchor);
        }

        if (node instanceof VariableDeclarationNode) {
            return;
        }

        for (ASTNode child : node.getChildren()) {
            collectUses(child, anchor);
        }
    }

    private void recordOwner(String owner, ASTNode anchor) {
        if (owner == null) return;
        if (!isHeapOwner(owner)) return;

        if (seen.add(owner)) {
            lastUseNode.put(owner, anchor);
        }
    }

    private boolean isHeapOwner(String name) {
        return true;
    }

    private String rootOwner(ASTNode expr) {

        if (expr instanceof VariableNode v)
            return v.getName();

        if (expr instanceof StructFieldAccessNode f)
            return rootOwner(f.getStructInstance());

        if (expr instanceof StructMethodCallNode m)
            return rootOwner(m.getStructInstance());

        if (expr instanceof StructUpdateNode u)
            return rootOwner(u.getTargetStruct());

        return null;
    }
}
