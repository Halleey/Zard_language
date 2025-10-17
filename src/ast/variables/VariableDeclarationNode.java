package ast.variables;
import ast.ASTNode;
import ast.lists.DynamicList;
import ast.lists.ListNode;
import ast.maps.DynamicMap;
import ast.maps.MapNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.runtime.StructDefinition;
import ast.structs.StructInstaceNode;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
            StructDefinition def = ctx.getStructType(type);
            value = new StructInstaceNode(type, def.getFields()).evaluate(ctx);
        } else {

            value = createInitialValue();
        }

        ctx.declareVariable(name, value);

        if (initializer != null) {
            if (initializer instanceof ListNode) {
                value = evaluateList(ctx, (ListNode) initializer, (DynamicList) value.getValue());
            } else if (initializer instanceof MapNode) {
                value = evaluateMap(ctx, (MapNode) initializer, (DynamicMap) value.getValue());
            } else {
                value = initializer.evaluate(ctx);
                ctx.setVariable(name, value);
            }
        }

        return value;
    }

    private boolean ctxHasStruct(RuntimeContext ctx, String typeName) {
        try {
            ctx.getStructType(typeName);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
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

            if (!keyVal.getType().equals(keyType)) {
                throw new RuntimeException("Tipo da chave incompatível na variável " + name +
                        ": esperado " + keyType + ", encontrado " + keyVal.getType());
            }
            if (!valueVal.getType().equals(valueType)) {
                throw new RuntimeException("Tipo do valor incompatível na variável " + name +
                        ": esperado " + valueType + ", encontrado " + valueVal.getType());
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
        } else {
            return createDefaultValue(type);
        }
    }

    private TypedValue createDefaultValue(String type) {
        return switch (type) {
            case "int" -> new TypedValue("int", 0);
            case "double" -> new TypedValue("double", 0.0);
            case "string" -> new TypedValue("string", "");
            case "boolean" -> new TypedValue("boolean", false);
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
}
