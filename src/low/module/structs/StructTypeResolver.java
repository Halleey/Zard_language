package low.module.structs;

import ast.ASTNode;
import ast.lists.ListGetNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import context.statics.symbols.ListType;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.main.TypeInfos;
import low.module.TypeTable;

import java.util.HashMap;
import java.util.Map;

public class StructTypeResolver {

    private final TypeTable types;
    private final StructRegistry structRegistry;
    private final Map<String, Type> listElementTypes = new HashMap<>();

    public StructTypeResolver(TypeTable types, StructRegistry structRegistry) {
        this.types = types;
        this.structRegistry = structRegistry;
    }

    public Type inferListElementType(ASTNode node) {

        if (node instanceof VariableNode v) {
            return getListElementType(v.getName());
        }

        if (node instanceof StructFieldAccessNode sfa) {

            Type fieldType = getStructFieldType(sfa);

            if (fieldType instanceof ListType lt) {
                return lt.elementType();
            }

            return null;
        }

        if (node instanceof ListGetNode lg) {
            return lg.getElementType();
        }

        return null;
    }

    public void registerListElementType(String varName, Type elementType) {

        if (varName != null && elementType != null) {
            listElementTypes.put(varName, elementType);
        }
    }

    public Type getListElementType(String varName) {

        if (varName == null) return null;

        return listElementTypes.get(varName);
    }

    public Type getStructFieldType(StructFieldAccessNode node) {

        ASTNode receiver = node.getStructInstance();
        String structName;

        if (receiver instanceof VariableNode varNode) {

            TypeInfos receiverInfo = types.getVarType(varNode.getName());

            if (receiverInfo == null) {
                throw new RuntimeException("Unknown receiver type for struct field access: " + node);
            }

            structName = extractStructName(receiverInfo.getType());
        }

        else if (receiver instanceof StructFieldAccessNode nested) {

            Type receiverType = getStructFieldType(nested);

            if (receiverType instanceof StructType st) {
                structName = st.name();
            } else {
                throw new RuntimeException("Receiver is not a struct: " + nested);
            }
        }

        else if (receiver instanceof ListGetNode lg) {

            Type elemType = lg.getElementType();

            if (elemType instanceof StructType st) {
                structName = st.name();
            } else {
                throw new RuntimeException("Cannot infer struct from ListGet: " + lg);
            }
        }

        else {
            throw new RuntimeException("Unsupported receiver in struct field access: " + receiver.getClass());
        }

        String normalized = normalizeStructKey(structName);

        StructNode structNode = structRegistry.get(normalized);

        if (structNode == null) {
            throw new RuntimeException("Struct not found: " + structName);
        }

        for (VariableDeclarationNode field : structNode.getFields()) {

            Type fieldType = field.getType();

            if (field.getName().equals(node.getFieldName())) {
                return fieldType;
            }
        }

        throw new RuntimeException(
                "Field not found: " + node.getFieldName() + " in struct " + structName
        );
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

    private String extractStructName(Type type) {

        if (type instanceof StructType st) {
            return st.name();
        }

        return type.toString();
    }

    /**
     * Resolve o nome da struct de um ASTNode
     */
    public String resolveStructName(ASTNode node) {

        if (node instanceof VariableNode varNode) {

            TypeInfos info = types.getVarType(varNode.getName());

            if (info == null) {
                throw new RuntimeException("Unknown variable: " + varNode.getName());
            }

            return extractStructName(info.getType());
        }

        if (node instanceof StructFieldAccessNode sfa) {

            Type fieldType = getStructFieldType(sfa);

            if (fieldType instanceof StructType st) {
                return st.name();
            }

            throw new RuntimeException("Field is not a struct: " + sfa);
        }

        if (node instanceof ListGetNode lg) {

            Type elemType = lg.getElementType();

            if (elemType instanceof StructType st) {
                return st.name();
            }

            throw new RuntimeException("List element is not a struct: " + lg);
        }

        throw new RuntimeException("Cannot resolve struct name from node: " + node);
    }
}