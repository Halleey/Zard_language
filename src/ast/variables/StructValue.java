package ast.variables;

import ast.expressions.TypedValue;
import memory_manager.borrows.AssignKind;


import java.util.LinkedHashMap;
import java.util.Map;
public class StructValue extends TypedValue {

    private final Map<String, TypedValue> fields;

    // OWNERSHIP
    private Object owner;
    private boolean moved;
    private AssignKind kind = AssignKind.COPY; // default

    public StructValue(String type, Map<String, TypedValue> fields) {
        super(type, fields);
        this.fields = fields;
        this.owner = null;
        this.moved = false;
        this.kind = AssignKind.COPY; // cópia por padrão
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public boolean isMoved() {
        return moved;
    }

    public AssignKind getKind() {
        return kind;
    }

    public void moveTo(Object newOwner) {
        if (moved) throw new RuntimeException("Struct já foi movida");
        this.owner = newOwner;
        this.moved = true;
        this.kind = AssignKind.MOVE;
    }

    public Map<String, TypedValue> getFields() {
        return fields;
    }

    public TypedValue deepCopy() {
        Map<String, TypedValue> copied = new LinkedHashMap<>();
        for (var e : fields.entrySet()) {
            copied.put(e.getKey(), e.getValue().deepCopy());
        }
        StructValue copy = new StructValue(type, copied);
        copy.kind = AssignKind.COPY;
        return copy;
    }
}
