package low.module;
// package low.module;

import ast.ASTNode;
import ast.lists.ListGetNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import low.main.TypeInfos;

import java.util.HashMap;
import java.util.Map;

public class StructTypeResolver {

    private final TypeTable types;
    private final StructRegistry structRegistry;
    private final Map<String, String> listElementTypes = new HashMap<>();

    public StructTypeResolver(TypeTable types, StructRegistry structRegistry) {
        this.types = types;
        this.structRegistry = structRegistry;
    }

    // ==== LIST TYPES INFERENCE ====
    public String inferListElementType(ASTNode node) {
        if (node instanceof VariableNode v) {
            return getListElementType(v.getName());
        }
        if (node instanceof StructFieldAccessNode sfa) {
            String fieldType = getStructFieldType(sfa);
            if (fieldType != null && fieldType.startsWith("List<") && fieldType.endsWith(">")) {
                return fieldType.substring(5, fieldType.length() - 1).trim();
            }
            return null;
        }
        if (node instanceof ListGetNode lg) {
            return inferListElementType(lg.getListName());
        }
        return null;
    }

    public void registerListElementType(String varName, String elementType) {
        if (varName == null || elementType == null) return;
        listElementTypes.put(varName, elementType);
    }

    public String getListElementType(String varName) {
        if (varName == null) return null;
        return listElementTypes.get(varName);
    }

    // ==== RESOLUÇÃO DE TIPOS DE CAMPOS DE STRUCT ====
    public String getStructFieldType(StructFieldAccessNode node) {
        String structName;

        if (node.getStructInstance() instanceof VariableNode varNode) {
            TypeInfos receiverInfo = types.getVarType(varNode.getName());
            if (receiverInfo == null) {
                throw new RuntimeException("Unknown receiver type for struct field access: " + node);
            }
            structName = receiverInfo.getSourceType()
                    .replace("%", "")
                    .replace("*", "");
        } else if (node.getStructInstance() instanceof StructFieldAccessNode nested) {
            String receiverType = getStructFieldType(nested);
            if (receiverType.startsWith("Struct<") && receiverType.endsWith(">")) {
                structName = receiverType.substring("Struct<".length(), receiverType.length() - 1);
            } else {
                structName = receiverType.replace("%", "").replace("*", "");
            }
        } else if (node.getStructInstance() instanceof ListGetNode lg) {
            String elem = inferListElementType(lg.getListName());
            if (elem == null) {
                throw new RuntimeException("Cannot infer element type from ListGet receiver: " + lg);
            }
            structName = elem.startsWith("Struct<") && elem.endsWith(">")
                    ? elem.substring("Struct<".length(), elem.length() - 1)
                    : elem;
        } else {
            throw new RuntimeException("Unsupported receiver in struct field access");
        }

        String normalized = normalizeStructKey(structName);
        StructNode structNode = structRegistry.get(normalized);
        if (structNode == null) {
            throw new RuntimeException("Struct not found: " + structName + " (normalized=" + normalized + ")");
        }

        for (VariableDeclarationNode field : structNode.getFields()) {
            if (field.getName().equals(node.getFieldName())) {
                return field.getType();
            }
        }

        throw new RuntimeException("Field not found: " + node.getFieldName() + " in struct " + structName);
    }

    // ==== RESOLVE STRUCT NAME FROM NODE ====
    public String resolveStructName(ASTNode node) {
        // variável simples
        if (node instanceof VariableNode varNode) {
            TypeInfos type = types.getVarType(varNode.getName());
            if (type != null) {
                String t = type.getSourceType();
                if (t.startsWith("Struct<") && t.endsWith(">")) {
                    return t.substring(7, t.length() - 1).trim();
                }
                if (t.startsWith("Struct ")) {
                    return t.substring(7).trim();
                }
                return t.replace("%", "").replace("*", "");
            }
            throw new RuntimeException("Unknown variable struct type: " + varNode.getName());
        }

        // acesso a campo de struct
        if (node instanceof StructFieldAccessNode sfa) {
            String parentType = getStructFieldType(sfa);
            if (parentType.startsWith("Struct<") && parentType.endsWith(">")) {
                return parentType.substring(7, parentType.length() - 1).trim();
            }
            if (parentType.startsWith("Struct ")) {
                return parentType.substring(7).trim();
            }
            return parentType.replace("%", "").replace("*", "");
        }

        // retorno de List.get() contendo struct
        if (node instanceof ListGetNode lg) {
            String elem = inferListElementType(lg.getListName());
            if (elem == null) {
                throw new RuntimeException("Cannot resolve struct name from list element type");
            }
            if (elem.startsWith("Struct<") && elem.endsWith(">")) {
                return elem.substring(7, elem.length() - 1).trim();
            }
            if (elem.startsWith("Struct ")) {
                return elem.substring(7).trim();
            }
            return elem.replace("%", "").replace("*", "");
        }

        throw new RuntimeException("Cannot resolve struct name from node type: " + node.getClass().getSimpleName());
    }

    private String normalizeStructKey(String name) {
        if (name == null) return null;
        name = name.trim();

        if (name.startsWith("Struct<") && name.endsWith(">")) {
            return name.substring(7, name.length() - 1).trim();
        }

        if (name.startsWith("Struct ")) {
            return name.substring(7).trim();
        }

        return name;
    }
}
