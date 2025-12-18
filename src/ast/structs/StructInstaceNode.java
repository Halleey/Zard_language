package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.lists.DynamicList;
import ast.lists.ListNode;
import ast.runtime.RuntimeContext;
import ast.runtime.StructDefinition;
import ast.variables.ListValue;
import ast.variables.StructValue;
import ast.variables.VariableDeclarationNode;
import low.module.LLVMEmitVisitor;
import memory_manager.borrows.OwnershipState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class StructInstaceNode extends ASTNode {

    private final String structName;
    private final List<ASTNode> positionalValues;
    private final Map<String, ASTNode> namedValues;
    private String concreteType;


    private OwnershipState initialOwnership = OwnershipState.OWNED;

    public OwnershipState getInitialOwnership() {
        return initialOwnership;
    }



    public StructInstaceNode(String structName,
                             List<ASTNode> positionalValues,
                             Map<String, ASTNode> namedValues) {
        this.structName = structName;
        this.positionalValues = (positionalValues != null) ? positionalValues : new ArrayList<>();
        this.namedValues = (namedValues != null) ? namedValues : new LinkedHashMap<>();
        this.concreteType = null;
    }

    public String getName() { return structName; }
    public List<ASTNode> getPositionalValues() { return positionalValues; }
    public Map<String, ASTNode> getNamedValues() { return namedValues; }

    public void setConcreteType(String t) { this.concreteType = t; }
    public String getConcreteType() { return concreteType; }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        StructDefinition def = ctx.getStructType(structName);
        List<VariableDeclarationNode> fields = def.getFields();

        Map<String, TypedValue> fieldValues = new LinkedHashMap<>();

        // Detecta atalho: struct com apenas um campo List<>
        VariableDeclarationNode listField = null;
        for (VariableDeclarationNode f : fields) {
            if (f.getType().startsWith("List<")) {
                if (listField == null) listField = f;
                else { listField = null; break; }
            }
        }

        boolean useListShortcut = (listField != null && !positionalValues.isEmpty());

        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            String fname = field.getName();
            String ftype = field.getType();

            TypedValue value;
            ASTNode astValue = null;

            if (!namedValues.isEmpty() && namedValues.containsKey(fname)) {
                astValue = namedValues.get(fname);
            } else if (!useListShortcut && i < positionalValues.size()) {
                astValue = positionalValues.get(i);
            }

            // =============================
            // LIST
            // =============================
            if (ftype.startsWith("List<")) {

                String innerType = ftype.substring(5, ftype.length() - 1);

                // Inferência para List<?> em struct genérica
                if (innerType.equals("?") && concreteType != null) {
                    String full = concreteType.substring("Struct<".length(), concreteType.length() - 1);
                    int open = full.indexOf('<');
                    int close = full.lastIndexOf('>');
                    if (open != -1 && close != -1) {
                        innerType = full.substring(open + 1, close);
                        ftype = "List<" + innerType + ">";
                    }
                }

                ListValue list = new ListValue(innerType, new ArrayList<>());

                // Atalho: valores posicionais
                if (useListShortcut && field == listField) {
                    for (ASTNode pv : positionalValues) {
                        list.add(pv.evaluate(ctx));
                    }
                }
                // Valor explícito
                else if (astValue != null) {
                    list.add(astValue.evaluate(ctx));
                }

                value = new TypedValue(ftype, list);
            }

            else if (ftype.startsWith("Struct<")) {
                String inner = ftype.substring("Struct<".length(), ftype.length() - 1);
                value = new StructInstaceNode(inner, null, null).evaluate(ctx);
            }

            else if (astValue != null) {
                value = astValue.evaluate(ctx);
            }

            else if (ftype.equals("string")) {
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

            fieldValues.put(fname, value);
        }

        return new StructValue(
                "Struct<" + structName + ">",
                fieldValues
        );
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "StructInstance " + structName +
                (concreteType != null ? " (" + concreteType + ")" : ""));

        if (!namedValues.isEmpty()) {
            for (var e : namedValues.entrySet()) {
                System.out.println(prefix + "  " + e.getKey() + " =");
                e.getValue().print(prefix + "    ");
            }
        } else if (!positionalValues.isEmpty()) {
            for (int i = 0; i < positionalValues.size(); i++) {
                System.out.println(prefix + "  field[" + i + "] =");
                ASTNode v = positionalValues.get(i);
                if (v != null) v.print(prefix + "    ");
                else System.out.println(prefix + "    <default>");
            }
        } else {
            System.out.println(prefix + "  <no field values>");
        }
    }

    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> list = new ArrayList<>(positionalValues);
        list.addAll(namedValues.values());
        return list;
    }
}
