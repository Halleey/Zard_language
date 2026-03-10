package ast.structs;

import ast.ASTNode;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.runtime.RuntimeContext;
import context.statics.structs.StaticFields;
import context.statics.structs.StaticStructDefinition;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import context.statics.symbols.UnknownType;
import low.module.LLVMEmitVisitor;

import java.util.List;
import java.util.Map;

public class StructFieldAccessNode extends ASTNode {

    private final ASTNode structInstance;
    private final String fieldName;
    private final ASTNode value; // null = acesso, != null = atribuição

    private Type type;

    public StructFieldAccessNode(ASTNode structInstance,
                                 String fieldName,
                                 ASTNode value) {
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
        Map<String, TypedValue> fields =
                (Map<String, TypedValue>) structVal.value();

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
    public void bindChildren(StaticContext ctx) {

        structInstance.setParent(this);
        structInstance.bind(ctx);

        if (value != null) {
            value.setParent(this);
            value.bind(ctx);
        }

        Type structType = structInstance.getType();

        if (!(structType instanceof StructType struct)) {
            throw new RuntimeException(
                    "Acesso de campo '" + fieldName +
                            "' em tipo não-struct: " + structType +
                            " (node=" + structInstance.getClass().getSimpleName() + ")"
            );
        }
        StaticStructDefinition def =
                ctx.resolveStruct(struct.name());

        StaticFields field = def.getField(fieldName);

        if (field == null) {
            throw new RuntimeException(
                    "Campo '" + fieldName +
                            "' não existe no struct " + struct.name()
            );
        }

        Type fieldType = field.getType();

        this.type = fieldType;

        // Se for atribuição, validar compatibilidade
        if (value != null) {

            Type valueType = value.getType();

            if (!isCompatible(fieldType, valueType)) {
                throw new RuntimeException(
                        "Type mismatch: campo '" + fieldName +
                                "' é " + fieldType +
                                " mas recebeu " + valueType
                );
            }
        }
    }

    private boolean isCompatible(Type expected, Type actual) {

        if (expected.equals(actual)) return true;

        if (expected instanceof PrimitiveTypes e &&
                actual   instanceof PrimitiveTypes a) {

            String en = e.name();
            String an = a.name();

            if (en.equals("double") && an.equals("int")) return true;
            if (en.equals("float")  && an.equals("int")) return true;
            if (en.equals("double") && an.equals("float")) return true;
        }

        if (actual instanceof UnknownType) return true;

        return false;
    }



    @Override
    public Type getType() {
        return type;
    }



    @Override
    public void print(String prefix) {

        if (value != null) {
            System.out.println(prefix + "StructFieldAssignment: " + fieldName);
            structInstance.print(prefix + "  Target:");
            value.print(prefix + "  Value:");
        } else {
            System.out.println(prefix + "StructFieldAccess: " + fieldName);
            structInstance.print(prefix + "  ");
        }
    }

    @Override
    public List<ASTNode> getChildren() {
        return value != null
                ? List.of(structInstance, value)
                : List.of(structInstance);
    }
}