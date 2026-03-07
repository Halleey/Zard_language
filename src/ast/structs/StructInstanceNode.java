package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.lists.ListNode;
import ast.variables.VariableDeclarationNode;
import context.runtime.RuntimeContext;
import context.runtime.StructDefinition;
import context.statics.StaticContext;
import context.statics.list.ListValue;
import context.statics.structs.StaticStructDefinition;
import context.statics.symbols.*;
import low.module.LLVMEmitVisitor;

import java.util.*;

public class StructInstanceNode extends ASTNode {

    private final String structName;
    private final List<ASTNode> positionalValues;
    private final Map<String, ASTNode> namedValues;

    private Type resolvedType;

    public StructInstanceNode(String structName,
                              List<ASTNode> positionalValues,
                              Map<String, ASTNode> namedValues) {

        this.structName = structName;
        this.positionalValues = positionalValues != null ? positionalValues : new ArrayList<>();
        this.namedValues = namedValues != null ? namedValues : new LinkedHashMap<>();
    }

    public String getName() { return structName; }

    public List<ASTNode> getPositionalValues() { return positionalValues; }

    public Map<String, ASTNode> getNamedValues() { return namedValues; }

    public Type getResolvedType() { return resolvedType; } // 🔥 NOVO

    @Override
    public Type getType() {
        return resolvedType;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void bindChildren(StaticContext stx) {

        StaticStructDefinition def = stx.resolveStruct(structName);

        if (def == null) {
            throw new RuntimeException("Struct não definida: " + structName);
        }

        this.resolvedType = new StructType(structName);

        for (String name : namedValues.keySet()) {
            def.getField(name);
        }

        if (!positionalValues.isEmpty() &&
                positionalValues.size() > def.getFields().size()) {

            throw new RuntimeException(
                    "Struct " + structName +
                            " recebe valores demais ("
                            + positionalValues.size() +
                            " > " + def.getFields().size() + ")"
            );
        }

        for (ASTNode n : positionalValues) {
            n.bind(stx);
        }

        for (ASTNode n : namedValues.values()) {
            n.bind(stx);
        }
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        StructDefinition def = ctx.getStructType(structName);

        Map<String, TypedValue> fieldValues = new LinkedHashMap<>();
        List<VariableDeclarationNode> fields = def.getFields();

        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            String fieldName = field.getName();
            Type fieldType = field.getType();

            ASTNode astValue = null;

            if (!namedValues.isEmpty() && namedValues.containsKey(fieldName)) {
                astValue = namedValues.get(fieldName);
            } else if (i < positionalValues.size()) {
                astValue = positionalValues.get(i);
            }

            TypedValue value;

            if (fieldType instanceof ListType listType) {

                Type innerType = listType.elementType();

                if (astValue != null) {

                    TypedValue tv = astValue.evaluate(ctx);

                    if (tv.value() instanceof ListValue) {
                        value = tv;
                    } else {
                        ListValue list =
                                new ListValue(innerType, isReferenceField(field));
                        list.add(tv);
                        value = new TypedValue(fieldType, list);
                    }

                } else {
                    value = new TypedValue(
                            fieldType,
                            new ListValue(innerType, isReferenceField(field))
                    );
                }
            }

            else if (fieldType instanceof StructType structType) {

                if (astValue != null) {
                    value = astValue.evaluate(ctx);
                } else {
                    value = new StructInstanceNode(
                            structType.name(),
                            null,
                            null
                    ).evaluate(ctx);
                }
            }

            else if (fieldType instanceof PrimitiveTypes prim) {

                if (astValue != null) {
                    value = astValue.evaluate(ctx);
                } else {
                    value = createPrimitiveDefault(prim);
                }
            }

            else {
                throw new RuntimeException("Tipo não suportado: " + fieldType);
            }

            fieldValues.put(fieldName, value);
        }

        return new TypedValue(resolvedType, fieldValues);
    }


    private TypedValue createPrimitiveDefault(PrimitiveTypes prim) {

        return switch (prim.name()) {

            case "int" -> new TypedValue(prim, 0);
            case "double" -> new TypedValue(prim, 0.0);
            case "float" -> new TypedValue(prim, 0.0f);
            case "boolean" -> new TypedValue(prim, false);
            case "string" -> new TypedValue(prim, "");
            case "char" -> new TypedValue(prim, '\0');

            default -> throw new RuntimeException(
                    "Tipo primitivo desconhecido: " + prim.name()
            );
        };
    }

    private boolean isReferenceField(VariableDeclarationNode field) {

        if (!(field.getInitializer() instanceof ListNode listNode)) {
            return false;
        }

        return listNode.isReference();
    }

    public void setResolvedType(Type type) {
        this.resolvedType = type;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "StructInstance " + structName);
    }

    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> list = new ArrayList<>(positionalValues);
        list.addAll(namedValues.values());
        return list;
    }
}