package memory_manager;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructMethodCallNode;
import ast.structs.StructUpdateNode;

import ast.variables.VariableNode;


import java.util.*;


public class DeterministicLifetimeAnalyzer {

    private final Map<String, String> varTypes; // seu getVariableTypes()
    private final Set<String> seen = new HashSet<>();
    private final Map<String, ASTNode> lastUseStmt = new HashMap<>();

    public DeterministicLifetimeAnalyzer(Map<String, String> varTypes) {
        this.varTypes = varTypes;
    }

    public Map<String, ASTNode> analyze(List<ASTNode> statements) {
        for (int i = statements.size() - 1; i >= 0; i--) {
            ASTNode stmt = statements.get(i);
            visit(stmt, stmt); // <-- stmtContext = o próprio statement
        }
        return lastUseStmt;
    }

    private void visit(ASTNode node, ASTNode stmtContext) {

        if (node instanceof VariableNode v) {
            markIfOwner(v.getName(), stmtContext);
        }

        if (node instanceof StructFieldAccessNode f) {
            String owner = rootOwnerName(f.getStructInstance());
            if (owner != null) markIfOwner(owner, stmtContext);
        }

        if (node instanceof StructMethodCallNode m) {
            String owner = rootOwnerName(m.getStructInstance());
            if (owner != null) markIfOwner(owner, stmtContext);
        }

        if (node instanceof StructUpdateNode u) {
            String owner = rootOwnerName(u.getTargetStruct());
            if (owner != null) markIfOwner(owner, stmtContext);
        }

        // visita filhos sempre
        for (ASTNode child : node.getChildren()) {
            visit(child, stmtContext);
        }
    }

    private void markIfOwner(String name, ASTNode stmtContext) {
        if (!isStructOwner(name)) return;
        if (seen.add(name)) lastUseStmt.put(name, stmtContext);
    }

    private boolean isStructOwner(String name) {
        String src = varTypes.get(name);
        return src != null && src.startsWith("Struct<");
    }

    private String rootOwnerName(ASTNode expr) {
        if (expr instanceof VariableNode v) return v.getName();
        if (expr instanceof StructFieldAccessNode f) return rootOwnerName(f.getStructInstance());
        if (expr instanceof StructMethodCallNode m) return rootOwnerName(m.getStructInstance());
        if (expr instanceof StructUpdateNode u) return rootOwnerName(u.getTargetStruct());
        return null;
    }
}
