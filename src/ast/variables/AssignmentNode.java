package ast.variables;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.symbols.*;
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

        Type expectedType = ctx.getVariable(name).type();
        Type actualType = value.type();

        if (!expectedType.equals(actualType)) {
            throw new RuntimeException(
                    "Erro de tipo: esperado " + expectedType +
                            " mas encontrado " + actualType
            );
        }

        ctx.setVariable(name, value);
        return value;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Assign: " + name);
        valueNode.print(prefix + "  ");
    }

    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public void bindChildren(StaticContext stx) {

        Symbol sym = stx.resolveVariable(name);
        Type expectedType = sym.getType();

        valueNode.bind(stx);
        Type actualType = valueNode.getType();

        if (actualType instanceof UnknownType) {
            throw new RuntimeException(
                    "Semantic error: cannot assign void value to variable '" + name + "'"
            );
        }

        checkTypeCompatibility(expectedType, actualType);
    }

    protected void checkTypeCompatibility(Type declared, Type current) {

        if (declared instanceof StructType ||
                current instanceof StructType) {
            return;
        }

        if (declared instanceof PrimitiveTypes dp && current instanceof PrimitiveTypes cp) {

            if (dp.name().equals(cp.name())) return;

            if (dp.name().equals("double") && cp.name().equals("int")) return;
            if (dp.name().equals("float")  && cp.name().equals("int")) return;
            if (dp.name().equals("double") && cp.name().equals("float")) return;
            if (dp.name().equals("float") && cp.name().equals("double")) return;
        }

        if (current instanceof InputType) return;

        throw new RuntimeException(
                "Semantic error: cannot assign value of type '" +
                        current + "' to variable of type '" +
                        declared + "'"
        );
    }
}
