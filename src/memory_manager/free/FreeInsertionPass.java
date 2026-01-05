package memory_manager.free;

import ast.ASTNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;
public class FreeInsertionPass {

    private final Map<ASTNode, List<FreeAction>> plan;

    public FreeInsertionPass(Map<ASTNode, List<FreeAction>> plan) {
        this.plan = plan;
    }

    public void insert(List<ASTNode> block) {
        List<ASTNode> newBlock = new ArrayList<>();

        for (ASTNode stmt : block) {
            newBlock.add(stmt);

            List<FreeAction> frees = plan.get(stmt);
            if (frees != null) {
                // insere frees **após** o nó
                for (FreeAction action : frees) {
                    newBlock.add(new FreeNode(action.getRoot()));
                }
            }
        }

        block.clear();
        block.addAll(newBlock);
    }
}
