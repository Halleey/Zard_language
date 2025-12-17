package ast.variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class AssignmentNode extends ASTNode {
    public final String name;
    public final ASTNode valueNode;

    public ASTNode getValueNode() {
        return valueNode;
    }

    public String getName() {
        return name;
    }

    public AssignmentNode(String name, ASTNode valueNode) {
        this.name = name;
        this.valueNode = valueNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        if (!ctx.hasVariable(name)) {
            throw new RuntimeException("Variável não declarada: " + name);
        }

        TypedValue value = valueNode.evaluate(ctx);

        String expectedType = ctx.getVariable(name).type();
        if (!expectedType.equals(value.type())) {
            throw new RuntimeException(
                    "Erro de tipo: esperado " + expectedType + " mas encontrado " + value.type()
            );
        }

        TypedValue assigned = value.deepCopy();

        ctx.setVariable(name, assigned);
        return assigned;
    }



    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Assign: " + name);
        valueNode.print(prefix + "  ");
    }
}
