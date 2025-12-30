package ast.context.statics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class StaticStructDefinition {

    private final String name;
    private final List<StaticFields> fields;
    private final Map<String, StaticFields> fieldMap;

    public StaticStructDefinition(String name, List<StaticFields> fields) {
        this.name = name;
        this.fields = List.copyOf(fields);
        this.fieldMap = new LinkedHashMap<>();

        for (StaticFields f : fields) {
            if (fieldMap.containsKey(f.getName())) {
                throw new RuntimeException(
                        "Campo duplicado no struct " + name + ": " + f.getName()
                );
            }
            fieldMap.put(f.getName(), f);
        }
    }

    public String getName() {
        return name;
    }

    public List<StaticFields> getFields() {
        return fields;
    }

    public StaticFields getField(String name) {
        StaticFields f = fieldMap.get(name);
        if (f == null) {
            throw new RuntimeException(
                    "Campo não existe no struct " + this.name + ": " + name
            );
        }
        return f;
    }
}
