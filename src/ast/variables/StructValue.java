package ast.variables;

import ast.expressions.TypedValue;


import java.util.LinkedHashMap;
import java.util.Map;

public class StructValue extends TypedValue {

    private final Map<String, TypedValue> fields;

    public StructValue(String type, Map<String, TypedValue> fields) {
        super(type, fields);
        this.fields = fields;
    }

    public Map<String, TypedValue> getFields() {
        return fields;
    }

    @Override
    public TypedValue deepCopy() {
        Map<String, TypedValue> copied = new LinkedHashMap<>();
        for (var e : fields.entrySet()) {
            copied.put(e.getKey(), e.getValue().deepCopy());
        }
        return new StructValue(type, copied);
    }
}
