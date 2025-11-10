package ast.variables;
import ast.ASTNode;
import ast.lists.DynamicList;
import ast.lists.ListNode;
import ast.maps.DynamicMap;
import ast.maps.MapNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.structs.StructInstaceNode;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VariableDeclarationNode extends ASTNode {
    private final String name;
    private final String type;
    public final ASTNode initializer;

    public VariableDeclarationNode(String name, String type, ASTNode initializer) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue value;

        if (ctxHasStruct(ctx, type)) {
            String structName = extractStructName(type);

            StructInstaceNode instanceNode;
            if (initializer instanceof StructInstaceNode) {
                instanceNode = (StructInstaceNode) initializer;
            } else {
                instanceNode = new StructInstaceNode(structName, null, null);
            }

            value = instanceNode.evaluate(ctx);

        } else {
            value = createInitialValue();
        }

        ctx.declareVariable(name, value);

        if (initializer != null && !(initializer instanceof StructInstaceNode)) {
            if (initializer instanceof ListNode) {
                value = evaluateList(ctx, (ListNode) initializer, (DynamicList) value.value());
            } else if (initializer instanceof MapNode) {
                value = evaluateMap(ctx, (MapNode) initializer, (DynamicMap) value.value());
            } else {
                value = initializer.evaluate(ctx);

                if ("float".equals(type) && "double".equals(value.type())) {
                    double d = (Double) value.value();
                    value = new TypedValue("float", (float) d);
                }
                ctx.setVariable(name, value);
            }
        }

        return value;
    }

    private boolean ctxHasStruct(RuntimeContext ctx, String typeName) {
        try {
            if (typeName.startsWith("Struct<")) {
                ctx.getStructType(extractStructName(typeName));
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private String extractStructName(String typeName) {
        if (typeName.startsWith("Struct<") && typeName.endsWith(">")) {
            return typeName.substring("Struct<".length(), typeName.length() - 1);
        }
        return typeName;
    }

    private TypedValue evaluateList(RuntimeContext ctx, ListNode listNode, DynamicList list) {
        for (ASTNode elem : listNode.getList().getElements()) {
            list.add(elem.evaluate(ctx));
        }
        return new TypedValue(type, list);
    }

    private TypedValue evaluateMap(RuntimeContext ctx, MapNode mapNode, DynamicMap map) {
        for (Map.Entry<ASTNode, ASTNode> e : mapNode.getDynamicMap().getEntries().entrySet()) {
            TypedValue keyVal = e.getKey().evaluate(ctx);
            TypedValue valueVal = e.getValue().evaluate(ctx);

            String declaredType = type;
            String keyType = declaredType.substring(declaredType.indexOf('<') + 1, declaredType.indexOf(','));
            String valueType = declaredType.substring(declaredType.indexOf(',') + 1, declaredType.length() - 1);

            if (!keyVal.type().equals(keyType)) {
                throw new RuntimeException("Tipo da chave incompatível na variável " + name +
                        ": esperado " + keyType + ", encontrado " + keyVal.type());
            }
            if (!valueVal.type().equals(valueType)) {
                throw new RuntimeException("Tipo do valor incompatível na variável " + name +
                        ": esperado " + valueType + ", encontrado " + valueVal.type());
            }

            map.put(keyVal, valueVal);
        }
        return new TypedValue(type, map);
    }

    public TypedValue createInitialValue() {
        if (type.startsWith("List<")) {
            String elementType = getListElementType(type);
            return new TypedValue(type, new DynamicList(elementType, new ArrayList<>()));
        } else if (type.startsWith("Map<")) {
            String keyType = type.substring(type.indexOf('<') + 1, type.indexOf(','));
            String valueType = type.substring(type.indexOf(',') + 1, type.length() - 1);
            return new TypedValue(type, new DynamicMap(keyType, valueType, new LinkedHashMap<>()));
        }
        else if (type.startsWith("Struct<")) {
            extractStructName(type);
            return new TypedValue(type, new LinkedHashMap<String, TypedValue>());
        }

        else {
            return createDefaultValue(type);
        }
    }

    private TypedValue createDefaultValue(String type) {
        if (type.startsWith("Struct<")) {
            return new TypedValue(type, new LinkedHashMap<String, TypedValue>());
        }
        if (type.startsWith("Struct")) {
            return new TypedValue(type, new LinkedHashMap<String, TypedValue>());
        }
        return switch (type) {
            case "int" -> new TypedValue("int", 0);
            case "double" -> new TypedValue("double", 0.0);
            case "string" -> new TypedValue("string", "");
            case "boolean" -> new TypedValue("boolean", false);
            case "float" -> new TypedValue("float", 0.0);
            case "char" -> new TypedValue("char", '\0');
            default -> throw new RuntimeException("Tipo desconhecido: " + type);
        };
    }

    private String getListElementType(String listType) {
        if (!listType.startsWith("List<") || !listType.endsWith(">")) {
            throw new RuntimeException("Tipo inválido de lista: " + listType);
        }
        return listType.substring(5, listType.length() - 1);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "VarDecl: " + type + " " + name);
        if (initializer != null) {
            System.out.println(prefix + " Initializer:");
            initializer.print(prefix + " ");
        }
    }

    public String getName() { return name; }
    public String getType() { return type; }

    @Override
    public List<ASTNode> getChildren() {
        if (initializer == null) return java.util.Collections.emptyList();
        return java.util.Collections.singletonList(initializer);
    }

    public ASTNode getInitializer() {
        return initializer;
    }


}