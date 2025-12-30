package ast.variables;

import ast.ASTNode;
import ast.context.runtime.RuntimeContext;
import ast.context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class VariableNode extends ASTNode {
    public final String name;
    
    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }


    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return ctx.getVariable(name);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Variable: " + name);
    }

    @Override
    public void bind(StaticContext stx) {
        stx.resolveVariable(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "VariableNode{" +
                "name='" + name + '\'' +
                '}';
    }
}
