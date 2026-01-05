package memory_manager.free;

import ast.ASTNode;

import java.util.List;
import java.util.Map;

public class FreeInsertionPass {
    private final Map<String, ASTNode> lastUse;


    public FreeInsertionPass(Map<String, ASTNode> lastUse) {
        this.lastUse = lastUse;

    }

    public void apply(List<ASTNode> stmts) {

    }


}
