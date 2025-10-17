package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.runtime.RuntimeContext;
import ast.runtime.StructDefinition;
import ast.variables.VariableDeclarationNode;
import low.module.LLVMEmitVisitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StructInstaceNode extends ASTNode {
    private final String name;
    private final List<VariableDeclarationNode> fields;

    public StructInstaceNode(String name, List<VariableDeclarationNode> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        StructDefinition def = ctx.getStructType(name);
        Map<String, TypedValue> fieldValues = new LinkedHashMap<>();

        for (VariableDeclarationNode field : def.getFields()) {
            fieldValues.put(field.getName(), field.createInitialValue());
        }

        return new TypedValue( name, fieldValues);
    }


    @Override
    public void print(String prefix) {
        System.out.println(prefix + "StructInstance: " + name);
        if (fields != null) {
            for (VariableDeclarationNode field : fields) {
                System.out.println(prefix + "  Field: " + field.getType() + " " + field.getName());
            }
        }
    }

}
