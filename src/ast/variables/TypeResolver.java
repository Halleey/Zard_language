package ast.variables;

import context.statics.symbols.*;

public final class TypeResolver {

    public static Type resolve(String typeStr) {

        if (typeStr == null || typeStr.isEmpty()) {
            throw new RuntimeException("Tipo inválido: vazio");
        }

        typeStr = typeStr.trim();

        switch (typeStr) {
            case "int": return PrimitiveTypes.INT;
            case "double": return PrimitiveTypes.DOUBLE;
            case "float": return PrimitiveTypes.FLOAT;
            case "bool":
            case "boolean": return PrimitiveTypes.BOOL;
            case "string": return PrimitiveTypes.STRING;
            case "void": return PrimitiveTypes.VOID;
            case "char": return PrimitiveTypes.CHAR;
            case "?": return UnknownType.UNKNOWN_TYPE;
        }

        if (typeStr.startsWith("Struct ")) {
            String name = typeStr.substring(7).trim();
            return new StructType(name);
        }

        if (typeStr.startsWith("List<") && typeStr.endsWith(">")) {

            String inner = typeStr.substring(5, typeStr.length() - 1);

            Type innerType = resolve(inner);

            return new ListType(innerType, true);
        }
        return new StructType(typeStr);
    }
}