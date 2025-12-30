package ast.loops;

import ast.ASTNode;
import context.statics.StaticContext;
import context.statics.ScopeKind;
import ast.exceptions.BreakLoop;
import ast.expressions.TypedValue;
import context.runtime.RuntimeContext;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class ForNode extends ASTNode {
    private final ASTNode init;
    private final ASTNode condition;
    private final ASTNode increment;
    private final List<ASTNode> body;

    public ASTNode getInit() {
        return init;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getIncrement() {
        return increment;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    public ForNode(ASTNode init, ASTNode condition, ASTNode increment, List<ASTNode> body) {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        // escopo DO FOR (init + cond + inc + body)
        RuntimeContext forCtx = new RuntimeContext(ctx);

        // INIT (ex: int i = 0)
        if (init != null) {
            init.evaluate(forCtx);
        }

        while (true) {

            // CONDITION
            if (condition != null) {
                TypedValue cond = condition.evaluate(forCtx);

                if (!(cond.value() instanceof Boolean)) {
                    throw new RuntimeException("Condition must be Boolean");
                }

                if (!((Boolean) cond.value())) {
                    break;
                }
            }

            // Escopo do CORPO
            RuntimeContext bodyCtx = new RuntimeContext(forCtx);

            try {
                for (ASTNode n : body) {
                    n.evaluate(bodyCtx);
                }
            } catch (BreakLoop ignored) {
                break;
            }

            // INCREMENT (ex: i++)
            if (increment != null) {
                increment.evaluate(forCtx);
            }
        }

        return null; // void
    }


    @Override
        public void print (String prefix){
            System.out.println(prefix + "For:");
            if (init != null) {
                System.out.println(prefix + "  Init:");
                init.print(prefix + "    ");
            }
            if (condition != null) {
                System.out.println(prefix + "  Condition:");
                condition.print(prefix + "    ");
            }
            if (increment != null) {
                System.out.println(prefix + "  Increment:");
                increment.print(prefix + "    ");
            }

            System.out.println(prefix + "  Body:");
            for (ASTNode node : body) node.print(prefix + "    ");
        }

    protected StaticContext childScope(ScopeKind kind, StaticContext parent) {
        return new StaticContext(kind, parent);
    }


    @Override
    public void bind(StaticContext stx) {

        StaticContext forCtx = childScope(ScopeKind.FOR, stx);

        if (init != null) init.bind(forCtx);
        if (condition != null) condition.bind(forCtx);
        if (increment != null) increment.bind(forCtx);

        StaticContext bodyCtx = childScope(ScopeKind.BLOCK, forCtx);
        for (ASTNode node : body) {
            node.bind(bodyCtx);
        }
    }


}
