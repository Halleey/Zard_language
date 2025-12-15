package memory_manager;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructMethodCallNode;
import ast.structs.StructUpdateNode;

import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;


import java.util.*;
public class DeterministicLifetimeAnalyzer {

    private final Map<String, String> varTypes; // nome -> sourceType
    private final Set<String> seen = new HashSet<>();
    private final Map<String, ASTNode> lastUseStmt = new HashMap<>();

    public DeterministicLifetimeAnalyzer(Map<String, String> varTypes) {
        this.varTypes = varTypes;
    }

    public Map<String, ASTNode> analyze(List<ASTNode> statements) {
        for (int i = statements.size() - 1; i >= 0; i--) {
            ASTNode stmt = statements.get(i);
            visit(stmt, stmt); // o próprio statement é o contexto
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

        if (node instanceof VariableDeclarationNode vd) {
            markIfOwner(vd.getName(), stmtContext);
        }


        if (node instanceof StructMethodCallNode m) {
            String owner = rootOwnerName(m.getStructInstance());
            if (owner != null) markIfOwner(owner, stmtContext);
        }
        if (node instanceof StructUpdateNode u) {
            String owner = rootOwnerName(u.getTargetStruct());
            if (owner != null) markIfOwner(owner, stmtContext);
        }

        for (ASTNode child : node.getChildren()) {
            visit(child, stmtContext);
        }
    }

    private void markIfOwner(String name, ASTNode stmtContext) {
        if (!isHeapOwner(name)) return;
        if (seen.add(name)) lastUseStmt.put(name, stmtContext);
    }

    private boolean isHeapOwner(String name) {
        String src = varTypes.get(name);
        if (src == null) return false;

        return src.startsWith("Struct<")
                || src.equals("string")
                || src.startsWith("List<");
    }


    private String rootOwnerName(ASTNode expr) {
        if (expr instanceof VariableNode v)
            return v.getName();

        if (expr instanceof StructFieldAccessNode f)
            return rootOwnerName(f.getStructInstance());

        if (expr instanceof StructMethodCallNode m)
            return rootOwnerName(m.getStructInstance());

        if (expr instanceof StructUpdateNode u)
            return rootOwnerName(u.getTargetStruct());

        return null;
    }
}
