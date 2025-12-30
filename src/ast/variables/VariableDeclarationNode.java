package ast.variables;
import ast.ASTNode;
import ast.context.statics.StaticContext;
import ast.lists.DynamicList;
import ast.lists.ListNode;

import ast.context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.structs.StructInstaceNode;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


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
            }else {
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
            String inner = typeName.substring("Struct<".length(), typeName.length() - 1);
            int genericIdx = inner.indexOf('<');
            if (genericIdx != -1) {
                inner = inner.substring(0, genericIdx);
            }
            return inner.trim();
        }
        return typeName;
    }



    private TypedValue evaluateList(RuntimeContext ctx, ListNode listNode, DynamicList list) {
        for (ASTNode elem : listNode.getList().getElements()) {
            list.add(elem.evaluate(ctx));
        }
        return new TypedValue(type, list);
    }

    public TypedValue createInitialValue() {
        if (type.startsWith("List<")) {
            String elementType = getListElementType(type);
            return new TypedValue(type, new DynamicList(elementType, new ArrayList<>()));
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
    @Override
    public String getType() { return type; }

    @Override
    public void bind(StaticContext stx) {
        stx.declareVariable(name, type);

        if (initializer != null) {
            initializer.bind(stx);
        }
    }


    @Override
    public List<ASTNode> getChildren() {
        if (initializer == null) return java.util.Collections.emptyList();
        return java.util.Collections.singletonList(initializer);
    }

    public ASTNode getInitializer() {
        return initializer;
    }




}