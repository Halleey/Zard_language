package ast.ifstatements;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.ScopeKind;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;

public class IfNode extends ASTNode {
    public final ASTNode condition;
    public  List<ASTNode> thenBranch;
    public  List<ASTNode> elseBranch; // pode ser null

    public ASTNode getCondition() {
        return condition;
    }
    public void setThenBranch(List<ASTNode> thenBranch) {
        this.thenBranch = new ArrayList<>(thenBranch);
    }

    public void setElseBranch(List<ASTNode> elseBranch) {
        if (elseBranch != null)
            this.elseBranch = new ArrayList<>(elseBranch);
        else
            this.elseBranch = null;
    }

    public List<ASTNode> getThenBranch() {
        return thenBranch;
    }

    public List<ASTNode> getElseBranch() {
        return elseBranch;
    }

    public IfNode(ASTNode condition, List<ASTNode> thenBranch, List<ASTNode> elseBranch) {
        this.condition = condition;
        this.thenBranch = new ArrayList<>(thenBranch);
        this.elseBranch = (elseBranch != null) ? new ArrayList<>(elseBranch) : null;
    }


    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue condVal = condition.evaluate(ctx);

        if (!(condVal.value() instanceof Boolean)) {
            throw new RuntimeException("Condição do if deve ser boolean");
        }

        if ((Boolean) condVal.value()) {
            for (ASTNode node : thenBranch) {
                node.evaluate(ctx);
            }
        } else if (elseBranch != null) {
            for (ASTNode node : elseBranch) {
                node.evaluate(ctx);
            }
        }

        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "If:");
        System.out.println(prefix + "  Condition:");
        condition.print(prefix + "    ");

        System.out.println(prefix + "  Then:");
        for (ASTNode n : thenBranch) n.print(prefix + "    ");

        if (elseBranch != null && !elseBranch.isEmpty()) {
            System.out.println(prefix + "  Else:");
            for (ASTNode n : elseBranch) n.print(prefix + "    ");
        }
    }

    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (condition != null) children.add(condition);
        if (thenBranch != null) children.addAll(thenBranch);
        if (elseBranch != null) children.addAll(elseBranch);
        return children;
    }
    @Override
    protected void bindChildren(StaticContext parent) {
        if (condition != null) {
            condition.setParent(this);
            condition.bind(parent);
        }

        if (thenBranch != null) {
            StaticContext thenCtx = new StaticContext(ScopeKind.IF_THEN, parent);
            for (ASTNode node : thenBranch) {
                node.setParent(this);
                node.bind(thenCtx);
            }
        }

        if (elseBranch != null) {
            StaticContext elseCtx = new StaticContext(ScopeKind.IF_ELSE, parent);
            for (ASTNode node : elseBranch) {
                node.setParent(this);
                node.bind(elseCtx);
            }
        }
    }


}
