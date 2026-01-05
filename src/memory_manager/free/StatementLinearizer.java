package memory_manager.free;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.ifstatements.IfNode;
import ast.lists.ListAddNode;
import ast.loops.WhileNode;
import ast.prints.PrintNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructUpdateNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;

import java.util.List;

public class StatementLinearizer {

    private int counter = 0;

    public void assign(List<ASTNode> stmts) {
        for (ASTNode stmt : stmts) {
            visit(stmt);
        }
    }

    private void visit(ASTNode node) {

        if (isStatement(node)) {
            node.setStmtId(counter++);
        }

        // controle de fluxo precisa preservar ordem
        if (node instanceof IfNode ifNode) {
            visit(ifNode.getCondition());
            ifNode.getThenBranch().forEach(this::visit);
            if (ifNode.getElseBranch() != null) {
                ifNode.getElseBranch().forEach(this::visit);
            }
            return;
        }

        if (node instanceof WhileNode whileNode) {
            visit(whileNode.getCondition());
            whileNode.getBody().forEach(this::visit);
            return;
        }

        if (node instanceof FunctionNode fn) {
            fn.getBody().forEach(this::visit);
            return;
        }

        // fallback
        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
    }

    private boolean isStatement(ASTNode node) {
        return node instanceof VariableDeclarationNode
                || node instanceof AssignmentNode
                || node instanceof PrintNode
                || node instanceof ReturnNode
                || node instanceof StructUpdateNode
                || node instanceof ListAddNode
                || node instanceof StructFieldAccessNode;
    }
}
