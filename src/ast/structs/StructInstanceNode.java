package ast.structs;

import ast.ASTNode;
import context.statics.StaticContext;
import context.statics.list.ListValue;
import context.statics.structs.StaticStructDefinition;
import ast.expressions.TypedValue;
import context.runtime.RuntimeContext;
import context.runtime.StructDefinition;
import ast.variables.VariableDeclarationNode;
import low.module.LLVMEmitVisitor;

import java.util.*;

public class StructInstanceNode extends ASTNode {
    private final String structName;
    private final List<ASTNode> positionalValues;
    private final Map<String, ASTNode> namedValues;
    private String concreteType;

    public StructInstanceNode(String structName,
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
        Map<String, TypedValue> fieldValues = new LinkedHashMap<>();
        List<VariableDeclarationNode> fields = def.getFields();

        VariableDeclarationNode listField = null;
        for (VariableDeclarationNode f : fields) {
            if (f.getType().startsWith("List<")) {
                if (listField == null) listField = f;
                else { listField = null; break; }
            }
        }

        boolean useListShortcut =
                listField != null && !positionalValues.isEmpty();

        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            String fname = field.getName();
            String ftype = field.getType();
            TypedValue value;

            if (useListShortcut && field == listField) {

                String innerType =
                        ftype.substring(5, ftype.length() - 1);

                ListValue listValue = new ListValue(innerType);

                for (ASTNode pv : positionalValues) {
                    listValue.add(pv.evaluate(ctx));
                }

                fieldValues.put(fname,
                        new TypedValue(ftype, listValue));
                continue;
            }

            ASTNode astValue = null;
            if (!namedValues.isEmpty() && namedValues.containsKey(fname)) {
                astValue = namedValues.get(fname);
            } else if (!useListShortcut && i < positionalValues.size()) {
                astValue = positionalValues.get(i);
            }

            if (ftype.startsWith("List<")) {

                String innerType =
                        ftype.substring(5, ftype.length() - 1);

                if (innerType.equals("?") && concreteType != null) {
                    String full =
                            concreteType.substring(7, concreteType.length() - 1);

                    if (full.contains("<")) {
                        innerType =
                                full.substring(full.indexOf('<') + 1,
                                        full.lastIndexOf('>'));
                        ftype = "List<" + innerType + ">";
                    }
                }

                if (astValue != null) {
                    TypedValue tv = astValue.evaluate(ctx);

                    if (tv.value() instanceof ListValue) {
                        value = tv;
                    } else {
                        ListValue list = new ListValue(innerType);
                        list.add(tv);
                        value = new TypedValue(ftype, list);
                    }
                } else {
                    // Lista vazia
                    value = new TypedValue(ftype,
                            new ListValue(innerType));
                }

            }
            else if (ftype.startsWith("Struct<")) {

                if (astValue != null) {
                    value = astValue.evaluate(ctx);
                } else {
                    String inner =
                            ftype.substring(7, ftype.length() - 1);
                    value = new StructInstanceNode(inner, null, null)
                            .evaluate(ctx);
                }

            }
            else if (astValue != null) {
                value = astValue.evaluate(ctx);
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

            fieldValues.put(fname, value);
        }

        return new TypedValue(
                "Struct<" + structName + ">", fieldValues
        );
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "StructInstance " + structName + (concreteType != null ? " (" + concreteType + ")" : ""));

        if (!namedValues.isEmpty()) {
            for (Map.Entry<String, ASTNode> e : namedValues.entrySet()) {
                System.out.println(prefix + "  " + e.getKey() + " =");
                e.getValue().print(prefix + "    ");
            }
        } else if (!positionalValues.isEmpty()) {
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

    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> list = new ArrayList<>(positionalValues);
        list.addAll(namedValues.values());
        return list;
    }
    @Override
    public void bind(StaticContext stx) {

        StaticStructDefinition def = stx.resolveStruct(structName);

        for (String name : namedValues.keySet()) {
            def.getField(name);
        }

        if (!positionalValues.isEmpty()) {
            if (positionalValues.size() > def.getFields().size()) {
                throw new RuntimeException(
                        "Struct " + structName +
                                " recebe valores demais (" +
                                positionalValues.size() +
                                " > " + def.getFields().size() + ")"
                );
            }
        }

        for (ASTNode n : positionalValues) {
            n.bind(stx);
        }

        for (ASTNode n : namedValues.values()) {
            n.bind(stx);
        }
    }

}