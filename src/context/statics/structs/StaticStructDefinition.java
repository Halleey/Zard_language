package context.statics.structs;

import ast.functions.FunctionNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;

import java.util.*;


public final class StaticStructDefinition {

    private final String name;
    private final List<StaticFields> fields;
    private final Map<String, StaticFields> fieldMap;
    private final Map<String, FunctionNode> methods = new LinkedHashMap<>();
    private final boolean isShared;

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

    public void addMethod(FunctionNode fn) {

        if (methods.containsKey(fn.getName())) {
            throw new RuntimeException(
                    "Método duplicado no struct " + name + ": " + fn.getName()
            );
        }

        methods.put(fn.getName(), fn);
    }

    public FunctionNode getMethod(String methodName) {
        return methods.get(methodName);
    }

    public Collection<FunctionNode> getMethods() {
        return methods.values();
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

    public boolean isShared() {
        return isShared;
    }

    public static StaticStructDefinition fromAST(StructNode node) {

        List<StaticFields> staticFields = new ArrayList<>();

        int index = 0;
        int offset = 0;

        for (VariableDeclarationNode field : node.getFields()) {

            Type fieldType = field.getResolvedType();

            staticFields.add(
                    new StaticFields(
                            field.getName(),
                            fieldType,
                            index++,
                            offset
                    )
            );

            offset += estimateSize(fieldType);
        }

        return new StaticStructDefinition(
                node.getName(),
                staticFields,
                false
        );
    }
    private static int estimateSize(Type type) {

        if (type instanceof PrimitiveTypes p) {

            return switch (p.name()) {
                case "int" -> 4;
                case "double" -> 8;
                case "float" -> 4;
                case "boolean", "bool" -> 1;
                case "char" -> 1;
                case "string" -> 8;
                default -> 8;
            };
        }

        if (type instanceof StructType) {
            return 8; // ponteiro
        }

        if (type instanceof ListType) {
            return 8; // ponteiro
        }

        throw new IllegalStateException("Tipo desconhecido: " + type);
    }
}