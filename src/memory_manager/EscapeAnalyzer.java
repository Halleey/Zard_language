package memory_manager;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.structs.ImplNode;
import ast.structs.StructInstaceNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;

import java.util.List;

public class EscapeAnalyzer {

    private final EscapeInfo info = new EscapeInfo();

    public EscapeInfo analyze(List<ASTNode> ast) {
        visitList(ast);
        return info;
    }

    private void visitList(List<ASTNode> nodes) {
        for (ASTNode n : nodes) {
            visit(n);
        }
    }

    private void visit(ASTNode node) {

        if (node instanceof VariableDeclarationNode vd) {
            info.declare(vd.getName());
        }

        if (node instanceof ReturnNode ret) {
            ASTNode val = ret.getExpr();

            if (val instanceof StructInstaceNode sin) {
                info.markEscapes("<inline_struct@" + sin.hashCode() + ">");
            }

            if (val instanceof VariableNode v) {

                info.markEscapes(v.getName());
            }
        }

        if (node instanceof ImplNode impl) {
            for (FunctionNode fn : impl.getMethods()) {
                visit(fn);
            }
        }
        if (node instanceof FunctionNode fn) {
            visitList(fn.getBody());
        }


        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
    }
}
