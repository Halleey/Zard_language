package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.runtime.RuntimeContext;
import ast.variables.StructValue;
import low.module.LLVMEmitVisitor;
import memory_manager.borrows.AssignKind;

import java.util.List;
import java.util.Map;

public class StructFieldAccessNode extends ASTNode {
    private final ASTNode structInstance;
    private final String fieldName;
    private final ASTNode value;
    private String resolvedFieldType;

    private AssignKind assignKind = AssignKind.MOVE;

    public AssignKind getAssignKind() {
        return assignKind;
    }

    public void setAssignKind(AssignKind kind) {
        this.assignKind = kind;
    }


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

        if (!(structVal instanceof StructValue svStruct)) {
            throw new RuntimeException("Não é uma struct: " + structInstance);
        }

        Map<String, TypedValue> fields = svStruct.getFields();

        TypedValue val;

        if (value != null) {
            val = value.evaluate(ctx);

            if (val instanceof StructValue svField) {
                // Se já tiver dono, copia profundamente
                if (svField.hasOwner()) {
                    svField = (StructValue) svField.deepCopy();
                }
                svField.moveTo(svStruct); // atribuição agora segura
                val = svField;           // garante que val seja a cópia
            }

            fields.put(fieldName, val);
            return val;
        }

        val = fields.get(fieldName);
        if (val == null) {
            throw new RuntimeException("Campo não existe: " + fieldName);
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

    public void setResolvedFieldType(String s) {
        this.resolvedFieldType = s;
    }

    public String getResolvedFieldType() {
        return resolvedFieldType;
    }

    @Override
    public List<ASTNode> getChildren() {
        return value != null ? List.of(structInstance, value)
                : List.of(structInstance);
    }
}