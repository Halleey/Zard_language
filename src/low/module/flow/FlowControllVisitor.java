package low.module.flow;

import ast.exceptions.BreakLoop;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import low.TempManager;
import low.ifs.IfEmitter;
import low.module.LLVisitorMain;
import low.whiles.WhileEmitter;

import java.util.ArrayDeque;
import java.util.Deque;
public class FlowControllVisitor {
    private final IfEmitter ifEmitter;
    private final WhileEmitter whileEmitter;
    private final Deque<String> loopEndStack = new ArrayDeque<>();
    private final LLVisitorMain root;

    public FlowControllVisitor(LLVisitorMain root, TempManager temps) {
        this.root = root;
        this.ifEmitter = new IfEmitter(temps, root);
        this.whileEmitter = new WhileEmitter(temps, root);
    }

    public String visit(WhileNode node) {
        return whileEmitter.emit(node);
    }

    public String visit(IfNode node) {
        return ifEmitter.emit(node);
    }

    public void pushLoopEnd(String label) {
        loopEndStack.push(label);
    }

    public void popLoopEnd() {
        loopEndStack.pop();
    }

    public String currentLoopEnd() {
        if (loopEndStack.isEmpty()) {
            throw new RuntimeException("Break fora de loop!");
        }
        return loopEndStack.peek();
    }
}

