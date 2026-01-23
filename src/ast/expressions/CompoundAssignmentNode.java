package ast.expressions;

import ast.ASTNode;
import ast.variables.VariableNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import low.module.LLVMEmitVisitor;



public class CompoundAssignmentNode extends ASTNode {

    private final String operator; // "+=" ou "-="
    private final VariableNode target;
    private final ASTNode expr;

    public String getOperator() {
        return operator;
    }

    public VariableNode getTarget() {
        return target;
    }

    public ASTNode getExpr() {
        return expr;
    }

    public CompoundAssignmentNode(String operator, VariableNode target, ASTNode expr) {
        this.operator = operator;
        this.target = target;
        this.expr = expr;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue left = ctx.getVariable(target.getName());
        TypedValue right = expr.evaluate(ctx);

        Object l = left.value();
        Object r = right.value();

        if (l instanceof Integer li && r instanceof Integer ri) {
            int result = operator.equals("+=") ? li + ri : li - ri;
            TypedValue tv = new TypedValue("int", result);
            ctx.setVariable(target.getName(), tv);
            return tv;
        }

        if (l instanceof Double ld && r instanceof Double rd) {
            double result = operator.equals("+=") ? ld + rd : ld - rd;
            TypedValue tv = new TypedValue("double", result);
            ctx.setVariable(target.getName(), tv);
            return tv;
        }

        throw new RuntimeException("Tipos incompatíveis em " + operator);
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "CompoundAssign: " + operator);
        target.print(prefix + "  ");
        expr.print(prefix + "  ");
    }

    @Override
    public void bindChildren(StaticContext stx) {
        target.setParent(this);
        target.bind(stx);

        expr.setParent(this);
        expr.bind(stx);
    }

}
