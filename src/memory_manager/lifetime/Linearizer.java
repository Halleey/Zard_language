package memory_manager.lifetime;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.lists.*;
import ast.loops.WhileNode;
import ast.prints.PrintNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructMethodCallNode;
import ast.structs.StructUpdateNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;

import java.util.ArrayList;
import java.util.List;

public class Linearizer {

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

        if (node instanceof FunctionNode) return;

        if (node instanceof FunctionCallNode call) {
            call.getArgs().forEach(arg -> analyzeNode(arg, out));
            System.out.println("entrou aqui no analyzer");
            out.add(call);
            return;
        }

        if (node instanceof IfNode ifn) {
            out.add(ifn);
            analyzeNode(ifn.getCondition(), out);
            ifn.getThenBranch().forEach(stmt -> analyzeNode(stmt, out));
            if (ifn.getElseBranch() != null)
                ifn.getElseBranch().forEach(stmt -> analyzeNode(stmt, out));
            return;
        }

        if (node instanceof WhileNode wn) {

            out.add(wn);

            analyzeNode(wn.getCondition(), out);

            for (ASTNode stmt : wn.getBody()) {
                analyzeNode(stmt, out);
            }
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
        if(node instanceof ListAddAllNode allNode) {
            analyzeNode(allNode.getTargetListNode(), out);
            for (ASTNode args : allNode.getArgs())
                analyzeNode(args, out);
            out.add(allNode);
            return;
        }

        if(node instanceof ListClearNode clearNode) {
            analyzeNode(clearNode.getListNode(), out);
            out.add(clearNode);
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