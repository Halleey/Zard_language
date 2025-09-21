package ast.prints;

import ast.ASTNode;

import java.util.List;
public class ASTPrinter {

    public static void printAST(List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            node.print("");
        }
    }
}
