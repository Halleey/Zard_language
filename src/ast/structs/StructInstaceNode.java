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
    private final Map<String, ASTNode> namedValues;

    public StructInstaceNode(String structName,
                             List<ASTNode> positionalValues,
                             Map<String, ASTNode> namedValues) {
        this.structName = structName;
        this.positionalValues = (positionalValues != null) ? positionalValues : new ArrayList<>();
        this.namedValues = (namedValues != null) ? namedValues : new LinkedHashMap<>();
    }

    public String getName() { return structName; }
    public List<ASTNode> getPositionalValues() { return positionalValues; }
    public Map<String, ASTNode> getNamedValues() { return namedValues; }
    public boolean isNamedInit() { return !namedValues.isEmpty(); }

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
            String fname = field.getName();

            ASTNode astValue = null;

            if (!namedValues.isEmpty() && namedValues.containsKey(fname)) {
                astValue = namedValues.get(fname);
            } else if (i < positionalValues.size()) {
                astValue = positionalValues.get(i);
            }

            TypedValue value = (astValue != null) ? astValue.evaluate(ctx) : field.createInitialValue();
            fieldValues.put(fname, value);
        }

        return new TypedValue("Struct<" + structName + ">", fieldValues);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "StructInstance " + structName );

        if (!namedValues.isEmpty()) {
            for (Map.Entry<String, ASTNode> e : namedValues.entrySet()) {
                System.out.println(prefix + "  " + e.getKey() + " =");
                e.getValue().print(prefix + "    ");
            }
        } else if (positionalValues != null && !positionalValues.isEmpty()) {
            for (int i = 0; i < positionalValues.size(); i++) {
                System.out.println(prefix + "  field[" + i + "] =");
                ASTNode astValue = positionalValues.get(i);
                if (astValue != null) astValue.print(prefix + "    ");
                else System.out.println(prefix + "    <default>");
            }
        } else {
            System.out.println(prefix + "  <no field values>");
        }

    }

}
