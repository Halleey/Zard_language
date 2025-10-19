package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.runtime.RuntimeContext;
import ast.runtime.StructDefinition;
import ast.variables.VariableDeclarationNode;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class StructInstaceNode extends ASTNode {
    private final String structName;
    private final List<ASTNode> positionalValues;

    public StructInstaceNode(String structName, List<ASTNode> positionalValues) {
        this.structName = structName;
        this.positionalValues = (positionalValues != null) ? positionalValues : new ArrayList<>();
    }


    public String getName() {
        return structName;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        StructDefinition def = ctx.getStructType(structName);
        Map<String, TypedValue> fieldValues = new LinkedHashMap<>();

        List<VariableDeclarationNode> fields = def.getFields();

        for (int i = 0; i < fields.size(); i++) {
            VariableDeclarationNode field = fields.get(i);

            ASTNode astValue = (positionalValues != null && i < positionalValues.size())
                    ? positionalValues.get(i)
                    : null;

            TypedValue value = (astValue != null) ? astValue.evaluate(ctx) : field.createInitialValue();

            fieldValues.put(field.getName(), value);
        }

        return new TypedValue("Struct<" + structName + ">", fieldValues);
    }

    public List<ASTNode> getPositionalValues() {
        return positionalValues;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "StructInstance: " + structName);

        if (positionalValues != null && !positionalValues.isEmpty()) {
            for (int i = 0; i < positionalValues.size(); i++) {
                System.out.println(prefix + "  FieldValue:");
                ASTNode astValue = positionalValues.get(i);
                if (astValue != null) {
                    astValue.print(prefix + "    ");
                } else {
                    System.out.println(prefix + "    <default>");
                }
            }
        } else {
            System.out.println(prefix + "  <no field values>");
        }
    }
}
