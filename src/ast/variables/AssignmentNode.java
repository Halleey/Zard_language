package ast.variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;
import memory_manager.borrows.AssignKind;

public class AssignmentNode extends ASTNode {
    public final String name;
    public final ASTNode valueNode;
    private AssignKind assignKind = AssignKind.MOVE; // default

    public void setAssignKind(AssignKind kind) {
        this.assignKind = kind;
    }

    public AssignKind getAssignKind() {
        return assignKind;
    }


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

        // 🔥 AQUI está o ponto chave
        if (value instanceof StructValue sv) {

            switch (assignKind) {

                case MOVE -> {
                    if (sv.hasOwner()) {
                        throw new RuntimeException(
                                "Struct já possui dono. Use copy ou deep copy."
                        );
                    }
                    sv.moveTo(name);
                    ctx.setVariable(name, sv);
                    return sv;
                }

                case COPY, DEEP_COPY -> {
                    StructValue copy = (StructValue) sv.deepCopy();
                    ctx.setVariable(name, copy);
                    return copy;
                }
            }
        }

        // Tipos primitivos continuam normais
        ctx.setVariable(name, value);
        return value;
    }


    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Assign: " + name);
        valueNode.print(prefix + "  ");
    }
}
