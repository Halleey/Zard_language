package ast.home;

import ast.ASTNode;
import ast.context.RuntimeContext;
import ast.context.StaticContext;
import ast.context.statics.ScopeKind;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;



public class MainAST extends ASTNode {
    public final List<ASTNode> body;

    public MainAST(List<ASTNode> body) {
        this.body = body;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        for (ASTNode n : body) {
            n.evaluate(ctx);
        }
        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Main:");
        for (ASTNode n : body) {
            n.print(prefix + "  ");
        }
    }

    @Override
    public List<ASTNode> getChildren() {
        return body;
    }

    @Override
    public void bind(StaticContext parent) {
        StaticContext mainCtx = new StaticContext(ScopeKind.GLOBAL, parent);

        for (ASTNode n : body) {
            n.bind(mainCtx);
        }
    }


}
