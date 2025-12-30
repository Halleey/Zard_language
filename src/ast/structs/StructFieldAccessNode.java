package ast.structs;

import ast.ASTNode;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.runtime.RuntimeContext;
import low.module.LLVMEmitVisitor;

import java.util.List;
import java.util.Map;

public class StructFieldAccessNode extends ASTNode {
    private final ASTNode structInstance;
    private final String fieldName;
    private final ASTNode value;

    public StructFieldAccessNode(ASTNode structInstance, String fieldName, ASTNode value) {
        this.structInstance = structInstance;
        this.fieldName = fieldName;
        this.value = value;
    }

    public ASTNode getStructInstance() {
        return structInstance;
    }

    public String getFieldName() {
        return fieldName;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue structVal = structInstance.evaluate(ctx);

        if (!(structVal.value() instanceof Map<?, ?>)) {
            throw new RuntimeException("Não é uma struct: " + structInstance);
        }

        @SuppressWarnings("unchecked")
        Map<String, TypedValue> fields = (Map<String, TypedValue>) structVal.value();

        TypedValue val;
        if (value != null) {
            val = value.evaluate(ctx);
            fields.put(fieldName, val);
        } else {
            val = fields.get(fieldName);
            if (val == null) {
                throw new RuntimeException("Campo não existe: " + fieldName);
            }
        }
        return val;
    }

    @Override
    public void print(String prefix) {
        if (value != null) {
            System.out.println(prefix + "StructFieldAssignment: " + fieldName);
            System.out.println(prefix + " Value:");
            value.print(prefix + "  ");
        } else {
            System.out.println(prefix + "StructFieldAccess: " + fieldName);
        }
    }

    @Override
    public List<ASTNode> getChildren() {
        return value != null ? List.of(structInstance, value)
                : List.of(structInstance);
    }

    @Override
    public void bind(StaticContext stx) {

    }
}