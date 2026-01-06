package ast.loops;

import ast.ASTNode;
import context.statics.StaticContext;
import context.statics.ScopeKind;
import ast.exceptions.BreakLoop;
import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;

public class WhileNode extends ASTNode {
    public final ASTNode condition;
    public final List<ASTNode> body;

    public ASTNode getCondition() {
        return condition;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    public WhileNode(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        while (true) {
            TypedValue condVal = condition.evaluate(ctx);
            if (!(condVal.value() instanceof Boolean)) {
                throw new RuntimeException("Condição do while deve ser boolean");
            }
            if (!((Boolean) condVal.value())) break;

            // Cria um novo escopo para esta iteração
            RuntimeContext loopCtx = new RuntimeContext(ctx);

            try {
                for (ASTNode node : body) {
                    node.evaluate(loopCtx);
                }
            } catch (BreakLoop ignored) {
                break;
            }
        }
        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "While:");
        System.out.println(prefix + "  Condition:");
        condition.print(prefix + "    ");
        System.out.println(prefix + "  Body:");
        for (ASTNode n : body) n.print(prefix + "    ");
    }

    protected StaticContext childScope(ScopeKind kind, StaticContext parent) {
        return new StaticContext(kind, parent);
    }

    @Override
    public void bind(StaticContext stx) {
        StaticContext whileContext = childScope(ScopeKind.WHILE, stx);

        if (condition != null)
            condition.bind(whileContext);

        StaticContext bodyContext = childScope(ScopeKind.BLOCK, whileContext);

        for (ASTNode node : body)
            node.bind(bodyContext);
    }

    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (condition != null) children.add(condition);
        if (body != null) children.addAll(body);
        return children;
    }



}
