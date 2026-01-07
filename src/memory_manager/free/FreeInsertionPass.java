package memory_manager.free;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FreeInsertionPass {

    private final Map<ASTNode, List<FreeAction>> plan;

    public FreeInsertionPass(Map<ASTNode, List<FreeAction>> plan) {
        this.plan = plan;
    }

    public void insert(List<ASTNode> block) {
        List<ASTNode> newBlock = new ArrayList<>();

        for (ASTNode stmt : block) {

            newBlock.add(stmt);

            insertIntoNode(stmt);

            List<FreeAction> frees = plan.get(stmt);
            if (frees != null) {
                for (FreeAction action : frees) {
                    newBlock.add(new FreeNode(action.root()));
                }
            }
        }

        block.clear();
        block.addAll(newBlock);
    }

    private void insertIntoNode(ASTNode node) {

        if (node instanceof IfNode ifn) {
            insert(ifn.getThenBranch());
            if (ifn.getElseBranch() != null) {
                insert(ifn.getElseBranch());
            }
            return;
        }

        if (node instanceof WhileNode wn) {
            insert(wn.getBody());
            return;
        }

        if (node instanceof FunctionNode fn) {
            insert(fn.getBody());
        }
    }
}
