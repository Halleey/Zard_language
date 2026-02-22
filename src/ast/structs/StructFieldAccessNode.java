package ast.structs;

import ast.ASTNode;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.runtime.RuntimeContext;
import context.statics.structs.StaticFields;
import context.statics.structs.StaticStructDefinition;
import low.module.LLVMEmitVisitor;

import java.util.List;
import java.util.Map;

public class StructFieldAccessNode extends ASTNode {
    private final ASTNode structInstance;
    private final String fieldName;
    private final ASTNode value;
    private String type;

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
    private boolean isCompatible(String expected, String actual) {
        if (expected.equals(actual)) return true;

        if (expected.equals("double") && actual.equals("int")) return true;
        if (expected.equals("float")  && actual.equals("int")) return true;

        return false;
    }

    @Override
    public String getType() {
        return type;
    }
    @Override
    public void bindChildren(StaticContext ctx) {

        structInstance.setParent(this);
        structInstance.bind(ctx);

        if (value != null) {
            value.setParent(this);
            value.bind(ctx);
        }

        String structType = structInstance.getType();

        if (structType == null) {
            throw new RuntimeException("StructFieldAccess: struct type is null");
        }

        boolean isStruct =
                structType.startsWith("Struct<")
                        || isPlainStruct(ctx, structType);

        if (!isStruct) {
            throw new RuntimeException(
                    "Acesso de campo '" + fieldName +
                            "' em tipo não-struct: " + structType
            );
        }

        String structName = structType.startsWith("Struct<")
                ? extractStructName(structType)
                : structType;

        StaticStructDefinition def = ctx.resolveStruct(structName);

        if (def == null) {
            throw new RuntimeException("Struct não encontrada: " + structName);
        }

        StaticFields field = def.getField(fieldName);
        String fieldType = field.getType();

        this.type = fieldType;

        if (value != null) {
            String valueType = value.getType();

            if (!isCompatible(fieldType, valueType)) {
                throw new RuntimeException(
                        "Type mismatch: campo '" + fieldName +
                                "' é " + fieldType +
                                " mas recebeu " + valueType
                );
            }
        }
    }

    private boolean isPlainStruct(StaticContext ctx, String type) {
        try {
            ctx.resolveStruct(type);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private String extractStructName(String structType) {
        return structType.substring(
                structType.indexOf('<') + 1,
                structType.lastIndexOf('>')
        );
    }
}