package prints;

import ast.ASTNode;
import ast.inputs.InputNode;
import home.MainAST;
import ifstatements.IfNode;
import loops.WhileNode;
import variables.*;

import java.util.List;
public class ASTPrinter {
    public static void printAST(List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            printNode(node, 0);
        }
    }

    private static void printNode(ASTNode node, int indent) {
        String prefix = "  ".repeat(indent);

        if (node instanceof MainAST main) {
            System.out.println(prefix + "Main:");
            for (ASTNode n : main.body) printNode(n, indent + 1);
        } else if (node instanceof VariableDeclarationNode decl) {
            System.out.println(prefix + "VarDecl: " + decl.type + " " + decl.name);
            if (decl.initializer != null) {
                System.out.println(prefix + "  Initializer:");
                printNode(decl.initializer, indent + 2);
            }
        } else if (node instanceof AssignmentNode assign) {
            System.out.println(prefix + "Assign: " + assign.name);
            printNode(assign.valueNode, indent + 1);
        } else if (node instanceof UnaryOpNode unary) {
            System.out.println(prefix + "UnaryOp: " + unary.name + " " + unary.operator);
        } else if (node instanceof BinaryOpNode bin) {
            System.out.println(prefix + "BinaryOp: " + bin.operator);
            printNode(bin.left, indent + 1);
            printNode(bin.right, indent + 1);
        } else if (node instanceof LiteralNode lit) {
            System.out.println(prefix + "Literal: (" + lit.value.getType() + ") " + lit.value.getValue());
        } else if (node instanceof VariableNode var) {
            System.out.println(prefix + "Variable: " + var.name);
        } else if (node instanceof PrintNode print) {
            System.out.println(prefix + "Print:");
            printNode(print.expr, indent + 1);
        }
        else if (node instanceof WhileNode whileNode){
            System.out.println(prefix + " while");
            System.out.println(prefix + " condition");
            printNode(whileNode.condition, indent +2);
            System.out.println(prefix + "  Body:");
            for (ASTNode n : whileNode.body) {
                printNode(n, indent + 2);
            }

        }
        else if(node instanceof InputNode inputNode){
            if (inputNode.getPrompt() != null && !inputNode.getPrompt().isEmpty()) {
                System.out.println(prefix + "  Prompt: \"" + inputNode.getPrompt() + "\"");
            }
        }
        else if (node instanceof IfNode ifNode) {
            System.out.println(prefix + "If:");
            System.out.println(prefix + "  Condition:");
            printNode(ifNode.condition, indent + 2);
            System.out.println(prefix + "  Then:");
            for (ASTNode n : ifNode.thenBranch) printNode(n, indent + 2);
            if (ifNode.elseBranch != null) {
                System.out.println(prefix + "  Else:");
                for (ASTNode n : ifNode.elseBranch) printNode(n, indent + 2);
            }
        }
        else {
            System.out.println(prefix + "Unknown Node: " + node.getClass().getSimpleName());
        }
    }
}