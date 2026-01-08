package ast.variables;
import ast.ASTNode;
import context.statics.StaticContext;

import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.structs.StructInstanceNode;
import context.statics.list.ListValue;
import low.module.LLVMEmitVisitor;

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

            StructInstanceNode instanceNode;
            if (initializer instanceof StructInstanceNode) {
                instanceNode = (StructInstanceNode) initializer;
            } else {
                instanceNode = new StructInstanceNode(structName, null, null);
            }

            value = instanceNode.evaluate(ctx);
            ctx.declareVariable(name, value);
            return value;
        }

        if (initializer == null) {
            value = createInitialValue();
            ctx.declareVariable(name, value);
            return value;
        }

        value = initializer.evaluate(ctx);

        if ("float".equals(type) && "double".equals(value.type())) {
            double d = (Double) value.value();
            value = new TypedValue("float", (float) d);
        }

        ctx.declareVariable(name, value);
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

    public TypedValue createInitialValue() {

        if (type.startsWith("List<")) {
            String elementType = getListElementType(type);
            return new TypedValue(type, new ListValue(elementType));
        }

        if (type.startsWith("Struct<")) {
            return new TypedValue(type, new LinkedHashMap<String, TypedValue>());
        }

        return createDefaultValue(type);
    }

    private TypedValue createDefaultValue(String type) {
        return switch (type) {
            case "int" -> new TypedValue("int", 0);
            case "double" -> new TypedValue("double", 0.0);
            case "float" -> new TypedValue("float", 0.0f);
            case "string" -> new TypedValue("string", "");
            case "boolean" -> new TypedValue("boolean", false);
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
    public void bindChildren(StaticContext ctx) {

        ctx.declareVariable(name, type);

        if (initializer != null) {
            initializer.bind(ctx);
        }
    }

    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "VarDecl: " + type + " " + name);
        if (initializer != null) {
            System.out.println(prefix + " Initializer:");
            initializer.print(prefix + " ");
        }
    }

    @Override
    public List<ASTNode> getChildren() {
        if (initializer == null) return List.of();
        return List.of(initializer);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    public ASTNode getInitializer() {
        return initializer;
    }
}
