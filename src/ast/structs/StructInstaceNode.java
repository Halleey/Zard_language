package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.lists.DynamicList;
import ast.lists.ListNode;
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
            String ftype = field.getType();

            ASTNode astValue = null;

            if (!namedValues.isEmpty() && namedValues.containsKey(fname)) {
                astValue = namedValues.get(fname);
            } else if (i < positionalValues.size()) {
                astValue = positionalValues.get(i);
            }

            TypedValue value;

            // Caso 1: se o campo é uma lista
            if (ftype.startsWith("List<")) {
                String innerType = ftype.substring(5, ftype.length() - 1);
                DynamicList dyn = new DynamicList(innerType, new ArrayList<>());

                // Se houver valores posicionais, adiciona-os à lista
                if (astValue != null) {
                    if (astValue instanceof ListNode listNode) {
                        for (ASTNode elem : listNode.getList().getElements()) {
                            dyn.add(elem.evaluate(ctx));
                        }
                    } else {
                        // Valor único adicionado
                        dyn.add(astValue.evaluate(ctx));
                    }
                }

                value = new TypedValue(ftype, dyn);
            }

            // Caso 2: valor explícito
            else if (astValue != null) {
                value = astValue.evaluate(ctx);
            }
            // Caso 3: inicialização automática
            else {
                if (ftype.startsWith("Struct<")) {
                    String inner = ftype.substring("Struct<".length(), ftype.length() - 1);
                    value = new StructInstaceNode(inner, null, null).evaluate(ctx);
                } else if (ftype.equals("string")) {
                    value = new TypedValue("string", "");
                } else if (ftype.equals("int")) {
                    value = new TypedValue("int", 0);
                } else if (ftype.equals("double") || ftype.equals("float")) {
                    value = new TypedValue(ftype, 0.0);
                } else if (ftype.equals("boolean")) {
                    value = new TypedValue("boolean", false);
                } else {
                    value = field.createInitialValue();
                }
            }

            fieldValues.put(fname, value);
        }

        return new TypedValue("Struct<" + structName + ">", fieldValues);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "StructInstance " + structName);

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
