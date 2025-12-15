package memory_manager;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import ast.structs.ImplNode;

import java.util.*;
public class FreeInsertionPass {

    private final Map<String, ASTNode> lastUse;

    public FreeInsertionPass(Map<String, ASTNode> lastUse) {
        this.lastUse = lastUse;
    }

    public void apply(List<ASTNode> stmts) {
        apply(stmts, null);
    }


    private void apply(List<ASTNode> stmts, ASTNode container) {
        if (stmts == null) return;

        ListIterator<ASTNode> it = stmts.listIterator();
        while (it.hasNext()) {
            ASTNode node = it.next();

            if (node instanceof MainAST main) {
                apply(main.body, main);
            } else if (node instanceof FunctionNode fn) {
                apply(fn.getBody(), fn);
            } else if (node instanceof ImplNode impl) {
                for (FunctionNode m : impl.getMethods()) {
                    apply(m.getBody(), m);
                }
            } else if (node instanceof WhileNode w) {
                apply(w.body, w);
            } else if (node instanceof IfNode iff) {
                apply(iff.thenBranch, iff);
                if (iff.elseBranch != null) apply(iff.elseBranch, iff);
            }

            // Inserção de pontos de liberação baseados no último uso
            for (var e : lastUse.entrySet()) {
                if (e.getValue() == node) {
                    it.add(new FreeNode(e.getKey()));
                }
            }
        }

        // Caso seja um container, insere um ponto de liberação após o término
        if (container != null) {
            for (var e : lastUse.entrySet()) {
                if (e.getValue() == container) {
                    stmts.add(new FreeNode(e.getKey()));
                }
            }
        }
    }
}
