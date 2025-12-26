package ast.structs;

import ast.ASTNode;
import ast.context.StaticContext;
import ast.expressions.TypedValue;
import ast.context.RuntimeContext;
import low.module.LLVMEmitVisitor;

import java.util.LinkedHashMap;
import java.util.Map;

public class StructUpdateNode extends ASTNode {
    private final ASTNode targetStruct; // Ex: p2.endereco
    private final Map<String, ASTNode> fieldUpdates; // Ex: rua -> "Nova Rua"
    private final Map<String, StructUpdateNode> nestedUpdates; // Ex: pais -> InlineStructUpdateNode

    public StructUpdateNode(ASTNode targetStruct,
                                  Map<String, ASTNode> fieldUpdates,
                                  Map<String, StructUpdateNode> nestedUpdates) {
        this.targetStruct = targetStruct;
        this.fieldUpdates = (fieldUpdates != null) ? fieldUpdates : new LinkedHashMap<>();
        this.nestedUpdates = (nestedUpdates != null) ? nestedUpdates : new LinkedHashMap<>();
    }

    public ASTNode getTargetStruct() { return targetStruct; }
    public Map<String, ASTNode> getFieldUpdates() { return fieldUpdates; }
    public Map<String, StructUpdateNode> getNestedUpdates() { return nestedUpdates; }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue targetVal = targetStruct.evaluate(ctx);

        if (!(targetVal.value() instanceof Map<?, ?> fieldsMap)) {
            throw new RuntimeException("Target não é uma struct: " + targetStruct);
        }

        @SuppressWarnings("unchecked")
        Map<String, TypedValue> fields = (Map<String, TypedValue>) fieldsMap;

        // Atualiza campos simples
        for (Map.Entry<String, ASTNode> entry : fieldUpdates.entrySet()) {
            String fieldName = entry.getKey();
            ASTNode expr = entry.getValue();
            TypedValue newVal = expr.evaluate(ctx);
            fields.put(fieldName, newVal);
        }

        // Atualiza structs aninhadas (de md recursivo)
        for (Map.Entry<String, StructUpdateNode> entry : nestedUpdates.entrySet()) {
            String fieldName = entry.getKey();
            StructUpdateNode nested = entry.getValue();
            TypedValue subStruct = fields.get(fieldName);

            if (subStruct == null || !(subStruct.value() instanceof Map<?, ?>)) {
                throw new RuntimeException("Campo interno não é uma struct: " + fieldName);
            }

            // executa a atualização recursiva
            nested.evaluateNested(ctx, subStruct);
        }

        return targetVal;
    }

    // usada para chamadas recursivas dentro do mesmo contexto
    private void evaluateNested(RuntimeContext ctx, TypedValue targetVal) {
        @SuppressWarnings("unchecked")
        Map<String, TypedValue> fields = (Map<String, TypedValue>) targetVal.value();

        for (Map.Entry<String, ASTNode> entry : fieldUpdates.entrySet()) {
            fields.put(entry.getKey(), entry.getValue().evaluate(ctx));
        }

        for (Map.Entry<String, StructUpdateNode> entry : nestedUpdates.entrySet()) {
            TypedValue nestedStruct = fields.get(entry.getKey());
            if (nestedStruct == null || !(nestedStruct.value() instanceof Map<?, ?>)) {
                throw new RuntimeException("Campo interno não é uma struct: " + entry.getKey());
            }
            entry.getValue().evaluateNested(ctx, nestedStruct);
        }
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "InlineStructUpdate:");
        System.out.println(prefix + " Target:");
        targetStruct.print(prefix + "  ");
        for (var e : fieldUpdates.entrySet()) {
            System.out.println(prefix + " Field " + e.getKey() + " =");
            e.getValue().print(prefix + "   ");
        }
        for (var e : nestedUpdates.entrySet()) {
            System.out.println(prefix + " Nested update on " + e.getKey() + ":");
            e.getValue().print(prefix + "   ");
        }
    }

    @Override
    public void bind(StaticContext stx) {

    }
}
