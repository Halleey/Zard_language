package ast.expressions;

import ast.ASTNode;
import ast.variables.TypeResolver;
import ast.variables.VariableNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;


public class ReferenceNode extends ASTNode {

    private final ASTNode target;

    public ReferenceNode(ASTNode target) {
        this.target = target;
    }

    public ASTNode getTarget() {
        return target;
    }

    @Override
    public LLVMValue accept(LLVMEmitVisitor visitor) {
        return null;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        if (!(target instanceof VariableNode var)) {
            throw new RuntimeException("Operator '&' require a variable");
        }
        return new TypedValue(TypeResolver.resolve("reference"), var);
    }

    @Override
    public void bindChildren(StaticContext stx) {
        target.bind(stx);
    }

    @Override
    public Type getType() {
        return target.getType(); //tipo base (ex: List<int>)
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "RefNode (&)");
        target.print(prefix + "  ");
    }
}