package memory_manager.lifetime;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructMethodCallNode;
import ast.structs.StructUpdateNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;

import java.util.*;

public class DeterministicLifetimeAnalyzer {

    private final Map<String, String> varTypes;

    private final Map<String, Integer> lastUseStmtId =
            new LinkedHashMap<>();

    private final Set<String> seen = new HashSet<>();

    public DeterministicLifetimeAnalyzer(Map<String, String> varTypes) {
        this.varTypes = varTypes;
    }

    public Map<String, Integer> analyze(List<ASTNode> roots) {

        List<ASTNode> linear =
                collectLinearStatements(roots);

        linear.sort(
                Comparator.comparingInt(ASTNode::getStmtId)
        );

        for (int i = linear.size() - 1; i >= 0; i--) {
            ASTNode stmt = linear.get(i);
            collectUses(stmt, stmt.getStmtId());
        }

        return lastUseStmtId;
    }

    private List<ASTNode> collectLinearStatements(List<ASTNode> roots) {
        List<ASTNode> out = new ArrayList<>();
        walk(roots, out);
        return out;
    }

    private void walk(List<ASTNode> nodes, List<ASTNode> out) {
        for (ASTNode node : nodes) {

            if (node.getStmtId() >= 0) {
                out.add(node);
            }

            // Controle de fluxo precisa preservar ordem
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


    private void collectUses(ASTNode node, int stmtId) {

        // Uso direto: variável
        if (node instanceof VariableNode v) {
            recordOwner(v.getName(), stmtId);
            return;
        }

        // Uso via campo
        if (node instanceof StructFieldAccessNode f) {
            String owner = rootOwner(f.getStructInstance());
            recordOwner(owner, stmtId);
        }

        // Uso via método
        if (node instanceof StructMethodCallNode m) {
            String owner = rootOwner(m.getStructInstance());
            recordOwner(owner, stmtId);
        }

        // Uso via update inline
        if (node instanceof StructUpdateNode u) {
            String owner = rootOwner(u.getTargetStruct());
            recordOwner(owner, stmtId);
        }

        // Declaração NÃO conta como uso
        if (node instanceof VariableDeclarationNode) {
            return;
        }

        for (ASTNode child : node.getChildren()) {
            collectUses(child, stmtId);
        }
    }


    private void recordOwner(String owner, int stmtId) {
        if (owner == null) return;
        if (!isHeapOwner(owner)) return;

        if (seen.add(owner)) {
            lastUseStmtId.put(owner, stmtId);
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
