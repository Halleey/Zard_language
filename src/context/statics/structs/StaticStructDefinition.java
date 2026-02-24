package context.statics.structs;

import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public final class StaticStructDefinition {

    private final String name;
    private final List<StaticFields> fields;
    private final Map<String, StaticFields> fieldMap;
    private final boolean isShared;

    public boolean isShared() {
        return isShared;
    }

    public StaticStructDefinition(String name, List<StaticFields> fields, boolean isShared) {
        this.name = name;
        this.fields = List.copyOf(fields);
        this.isShared = isShared;
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

    public StaticFields getField(String fieldName) {
        StaticFields f = fieldMap.get(fieldName);
        if (f == null) {
            throw new RuntimeException(
                    "Campo não existe no struct " + name + ": " + fieldName
            );
        }
        return f;
    }


    public static StaticStructDefinition fromAST(StructNode node) {

        List<StaticFields> staticFields = new ArrayList<>();

        int index = 0;
        int offset = 0;

        for (VariableDeclarationNode field : node.getFields()) {

            String type = field.getType();

            staticFields.add(
                    new StaticFields(
                            field.getName(),
                            type,
                            index++,
                            offset
                    )
            );

            offset += estimateSize(type); // 👈 importante
        }

        return new StaticStructDefinition(
                node.getName(),
                staticFields,
                false
        );
    }

    private static int estimateSize(String type) {
        return switch (type) {
            case "int" -> 4;
            case "double" -> 8;
            case "float" -> 4;
            case "boolean", "bool" -> 1;
            case "char" -> 1;
            case "string" -> 8; // ponteiro
            default -> 8; // structs / listas / refs
        };
    }
}
