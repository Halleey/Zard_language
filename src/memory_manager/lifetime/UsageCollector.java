package memory_manager.lifetime;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.ifstatements.IfNode;
import ast.lists.ListAddNode;
import ast.lists.ListGetNode;
import ast.lists.ListRemoveNode;
import ast.lists.ListSizeNode;
import ast.loops.WhileNode;
import ast.prints.PrintNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructMethodCallNode;
import ast.structs.StructUpdateNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class UsageCollector {

    private final Map<String, String> varTypes;
    private final Map<String, ASTNode> lastUseNode = new LinkedHashMap<>();
    private List<ASTNode> linearizedStatements;

    public UsageCollector(Map<String, String> varTypes) {
        this.varTypes = varTypes;
    }

    public void collect(List<ASTNode> linearizedStatements) {
        this.linearizedStatements = linearizedStatements;
        for (int i = linearizedStatements.size() - 1; i >= 0; i--) {
            ASTNode stmt = linearizedStatements.get(i);
            collectUses(stmt, stmt);
        }
    }

    public Map<String, ASTNode> getLastUseNode() {
        return lastUseNode;
    }

    private void collectUses(ASTNode node, ASTNode anchor) {
        if (node == null) return;

        if (node instanceof VariableNode v) {
            recordOwner(v.getName(), anchor);
            return;
        }

        if (node instanceof VariableDeclarationNode decl) {
            if (decl.getInitializer() != null) collectUses(decl.getInitializer(), anchor);
            return;
        }

        if (node instanceof FunctionCallNode call) {
            call.getArgs().forEach(arg -> collectUses(arg, anchor));
            return;
        }

        if (node instanceof AssignmentNode a) {
            collectUses(a.getValueNode(), anchor);
            return;
        }

        if (node instanceof PrintNode p) {
            collectUses(p.expr, anchor);
            return;
        }

        if (node instanceof ListAddNode add) {
            collectUses(add.getListNode(), anchor);
            collectUses(add.getValuesNode(), anchor);
            return;
        }

        if (node instanceof ListGetNode get) {
            collectUses(get.getListName(), anchor);
            collectUses(get.getIndexNode(), anchor);
            return;
        }

        if (node instanceof ListSizeNode size) {
            collectUses(size.getNome(), anchor);
            return;
        }

        if (node instanceof ListRemoveNode rem) {
            collectUses(rem.getListNode(), anchor);
            collectUses(rem.getIndexNode(), anchor);
            return;
        }

        if (node instanceof StructFieldAccessNode f) {
            recordOwner(rootOwner(f.getStructInstance()), anchor);
            return;
        }

        if (node instanceof StructMethodCallNode m) {
            recordOwner(rootOwner(m.getStructInstance()), anchor);
            m.getArgs().forEach(arg -> collectUses(arg, anchor));
            return;
        }

        if (node instanceof StructUpdateNode u) {
            recordOwner(rootOwner(u.getTargetStruct()), anchor);
            return;
        }

        if (node.getChildren() != null)
            node.getChildren().forEach(child -> collectUses(child, anchor));
    }

    private void recordOwner(String owner, ASTNode anchor) {
        if (owner == null || !isHeapOwner(owner)) return;
        ASTNode block = findEnclosingBlock(anchor);
        lastUseNode.putIfAbsent(owner, block != null ? block : anchor);
    }

    private ASTNode findEnclosingBlock(ASTNode node) {
        for (ASTNode stmt : linearizedStatements) {
            if ((stmt instanceof WhileNode || stmt instanceof IfNode) &&
                    contains(stmt, node)) return stmt;
        }
        return null;
    }

    private boolean contains(ASTNode parent, ASTNode child) {
        if (parent == child) return true;
        if (parent.getChildren() != null)
            for (ASTNode c : parent.getChildren())
                if (contains(c, child)) return true;
        return false;
    }

    private boolean isHeapOwner(String name) {
        return true;
    }

    private String rootOwner(ASTNode expr) {
        if (expr instanceof VariableNode v) return v.getName();
        if (expr instanceof StructFieldAccessNode f) return rootOwner(f.getStructInstance());
        if (expr instanceof StructMethodCallNode m) return rootOwner(m.getStructInstance());
        if (expr instanceof StructUpdateNode u) return rootOwner(u.getTargetStruct());
        return null;
    }
}
