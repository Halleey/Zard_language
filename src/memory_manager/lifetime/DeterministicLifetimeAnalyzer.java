package memory_manager.lifetime;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
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

import java.util.*;
import java.util.*;



import java.util.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeterministicLifetimeAnalyzer {

    private final Map<String, String> varTypes;
    private final Map<String, ASTNode> lastUseNode = new LinkedHashMap<>();
    private final Set<String> seen = new HashSet<>();

    public DeterministicLifetimeAnalyzer(Map<String, String> varTypes) {
        this.varTypes = varTypes;
    }

    public Map<String, ASTNode> analyzeAndReturnNode(List<ASTNode> roots) {
        List<ASTNode> linear = collectLinearStatements(roots);
        for (int i = linear.size() - 1; i >= 0; i--) {
            ASTNode stmt = linear.get(i);
            collectUses(stmt, stmt);
        }
        return lastUseNode;
    }

    private List<ASTNode> collectLinearStatements(List<ASTNode> roots) {
        List<ASTNode> out = new ArrayList<>();
        for (ASTNode node : roots) {
            analyzeNodeForLinearization(node, out);
        }
        return out;
    }

    private void analyzeNodeForLinearization(ASTNode node, List<ASTNode> out) {
        if (node == null) return;

        if (node instanceof MainAST main) {
            for (ASTNode stmt : main.getBody()) {
                analyzeNodeForLinearization(stmt, out);
            }
            return;
        }

        if (node instanceof FunctionNode fn) {
            for (ASTNode stmt : fn.getBody()) {
                analyzeNodeForLinearization(stmt, out);
            }
            return;
        }

        if (node instanceof IfNode ifn) {
            out.add(ifn);
            for (ASTNode stmt : ifn.getThenBranch()) {
                analyzeNodeForLinearization(stmt, out);
            }
            if (ifn.getElseBranch() != null) {
                for (ASTNode stmt : ifn.getElseBranch()) {
                    analyzeNodeForLinearization(stmt, out);
                }
            }
            return;
        }

        if (node instanceof WhileNode wn) {
            out.add(wn);
            for (ASTNode stmt : wn.getBody()) {
                analyzeNodeForLinearization(stmt, out);
            }
            return;
        }

        if (node.isStatement()) {
            out.add(node);
        }

        if (node.getChildren() != null) {
            for (ASTNode child : node.getChildren()) {
                analyzeNodeForLinearization(child, out);
            }
        }
    }private void collectUses(ASTNode node, ASTNode anchor) {
        if (node == null) return;

        if (node instanceof VariableNode v) {
            recordOwner(v.getName(), anchor);
            return;
        }

        if (node instanceof VariableDeclarationNode decl) {
            recordOwner(decl.getName(), decl);
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
            for (ASTNode arg : m.getArgs()) {
                collectUses(arg, anchor);
            }
            return;
        }

        if (node instanceof StructUpdateNode u) {
            String owner = rootOwner(u.getTargetStruct());
            recordOwner(owner, anchor);
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
