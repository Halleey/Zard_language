package ast.variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;


public class VariableNode extends ASTNode {

    private final String name;
    private VariableDeclarationNode declaration;

    public VariableNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void bind(VariableDeclarationNode decl) {
        this.declaration = decl;
    }

    public VariableDeclarationNode getDeclaration() {
        return declaration;
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
}
