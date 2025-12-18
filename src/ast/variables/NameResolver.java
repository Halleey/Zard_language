package ast.variables;

import ast.ASTNode;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructUpdateNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class NameResolver {

    private final Deque<Map<String, VariableDeclarationNode>> scopes = new ArrayDeque<>();

    public void resolve(ASTNode root) {
        enterScope();
        visit(root);
        exitScope();
    }

    private void visit(ASTNode node) {
        if (node == null) return;

        if (node instanceof VariableDeclarationNode vd) {
            declare(vd.getName(), vd);
        }

        if (node instanceof VariableNode vn) {
            bindVariable(vn);
        }

        if (node instanceof AssignmentNode an) {
            visit(an.getValueNode());

            VariableDeclarationNode decl = lookup(an.getName());
            if (decl == null) {
                throw new RuntimeException("❌ Variável '" + an.getName() + "' não declarada.");
            }
//             VariableNode vn = new VariableNode(an.getName());
//             vn.bind(decl);

            return;
        }

        if (node instanceof StructFieldAccessNode fa) {
            visit(fa.getStructInstance());
            visit(fa.getValue());
            return;
        }

        if (node instanceof StructUpdateNode isu) {
            visit(isu.getTargetStruct());
            for (ASTNode fieldValue : isu.getFieldUpdates().values()) {
                visit(fieldValue);
            }
            return;
        }

        if (node instanceof IfNode || node instanceof WhileNode) {
            enterScope();
            for (ASTNode child : node.getChildren()) {
                visit(child);
            }
            exitScope();
            return;
        }

        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
    }


    private void bindVariable(VariableNode vn) {
        VariableDeclarationNode decl = lookup(vn.getName());
        if (decl == null) {
            throw new RuntimeException(
                    "❌ Variável '" + vn.getName() + "' não declarada em nenhum escopo."
            );
        }
        vn.bind(decl);
    }

    private void enterScope() {
        scopes.push(new HashMap<>());
    }

    private void exitScope() {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("Tentando sair de um escopo inexistente");
        }
        scopes.pop();
    }

    private void declare(String name, VariableDeclarationNode decl) {
        Map<String, VariableDeclarationNode> current = scopes.peek();
        if (current.containsKey(name)) {
            throw new RuntimeException("❌ Variável '" + name + "' já declarada neste escopo.");
        }
        current.put(name, decl);
    }

    private VariableDeclarationNode lookup(String name) {
        for (Map<String, VariableDeclarationNode> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }
}
