package ast.home;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;

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
    public LLVMValue accept(LLVMEmitVisitor visitor) {
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
    public void bindChildren(StaticContext ctx) {
        for (ASTNode stmt : body) {
            stmt.bind(ctx);
        }
    }


}
