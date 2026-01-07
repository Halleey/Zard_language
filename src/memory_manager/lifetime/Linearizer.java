package memory_manager.lifetime;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
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

import java.util.ArrayList;
import java.util.List;

class Linearizer {

    public List<ASTNode> collectLinearStatements(List<ASTNode> roots) {
        List<ASTNode> out = new ArrayList<>();
        for (ASTNode node : roots) analyzeNode(node, out);
        return out;
    }

    private void analyzeNode(ASTNode node, List<ASTNode> out) {
        if (node == null) return;

        if (node instanceof MainAST main) {
            main.getBody().forEach(stmt -> analyzeNode(stmt, out));
            return;
        }

        if (node instanceof FunctionNode fn) {
            out.add(fn); // opcional
            return;
        }

        if (node instanceof FunctionCallNode call) {
            call.getArgs().forEach(arg -> analyzeNode(arg, out));
            out.add(call);
            return;
        }

        if (node instanceof IfNode ifn) {
            out.add(ifn);
            analyzeNode(ifn.getCondition(), out);
            ifn.getThenBranch().forEach(stmt -> analyzeNode(stmt, out));
            if (ifn.getElseBranch() != null) {
                ifn.getElseBranch().forEach(stmt -> analyzeNode(stmt, out));
            }
            return;
        }

        if (node instanceof WhileNode wn) {
            out.add(wn);
            analyzeNode(wn.getCondition(), out);
            wn.getBody().forEach(stmt -> analyzeNode(stmt, out));
            return;
        }

        if (node instanceof VariableDeclarationNode decl) {
            if (decl.getInitializer() != null) analyzeNode(decl.getInitializer(), out);
            out.add(decl);
            return;
        }

        if (node instanceof AssignmentNode assign) {
            analyzeNode(assign.getValueNode(), out);
            out.add(assign);
            return;
        }

        if (node instanceof PrintNode print) {
            analyzeNode(print.expr, out);
            out.add(print);
            return;
        }

        if (node instanceof ListAddNode add) {
            analyzeNode(add.getListNode(), out);
            analyzeNode(add.getValuesNode(), out);
            out.add(add);
            return;
        }

        if (node instanceof ListRemoveNode rem) {
            analyzeNode(rem.getListNode(), out);
            analyzeNode(rem.getIndexNode(), out);
            out.add(rem);
            return;
        }

        if (node instanceof ListGetNode get) {
            analyzeNode(get.getListName(), out);
            analyzeNode(get.getIndexNode(), out);
            out.add(get);
            return;
        }

        if (node instanceof ListSizeNode size) {
            analyzeNode(size.getNome(), out);
            out.add(size);
            return;
        }

        if (node instanceof StructFieldAccessNode ||
                node instanceof StructUpdateNode ||
                node instanceof StructMethodCallNode) {
            out.add(node);
            return;
        }

        if (node.isStatement()) out.add(node);

        if (node.getChildren() != null)
            node.getChildren().forEach(child -> analyzeNode(child, out));
    }
}
