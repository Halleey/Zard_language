package ast.structs;

import ast.ASTNode;
import context.statics.StaticContext;
import context.statics.structs.StaticFields;
import context.statics.structs.StaticStructDefinition;
import ast.expressions.TypedValue;
import context.runtime.RuntimeContext;
import ast.variables.VariableDeclarationNode;
import low.module.LLVMEmitVisitor;

import java.util.*;
import java.util.List;

public class StructNode extends ASTNode {
    private final String name;
    private final List<VariableDeclarationNode> fields;
    private String llvmName; // nome LLVM único, ex: Set_int, Set_double
    private boolean shared = false;


    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public StructNode(String name, List<VariableDeclarationNode> fields) {
        this.name = name;
        this.fields = fields;
        this.llvmName = name;
    }

    public Map<String, String> getFieldMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (VariableDeclarationNode f : fields) {
            map.put(f.getName(), f.getType());
        }
        return map;
    }



    public String getName() {
        return name;
    }

    public String getLLVMName() {
        return llvmName;
    }

    public void setLLVMName(String llvmName) {
        this.llvmName = llvmName;
    }

    public List<VariableDeclarationNode> getFields() {
        return fields;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ctx.registerStructType(name, fields);
        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Struct " + name);
        for (VariableDeclarationNode f : fields) {
            System.out.println(prefix + "  " + f.getName() + " : " + f.getType());
        }
    }

    @Override
    public void bindChildren(StaticContext stx) {

        Set<String> seen = new HashSet<>();
        for (VariableDeclarationNode f : fields) {
            if (!seen.add(f.getName())) {
                throw new RuntimeException(
                        "Campo duplicado no struct " + name + ": " + f.getName()
                );
            }
        }

        List<StaticFields> staticFields = new ArrayList<>();

        int index = 0;
        int offset = 0;

        for (VariableDeclarationNode f : fields) {
            staticFields.add(
                    new StaticFields(
                            f.getName(),
                            f.getType(),
                            index++,
                            offset++
                    )
            );
        }

        StaticStructDefinition def = new StaticStructDefinition(name, staticFields);

        stx.declareStruct(name, def);
    }


    public StructNode cloneWithType(String elemType) {
        List<VariableDeclarationNode> clonedFields = new ArrayList<>();
        for (VariableDeclarationNode f : fields) {
            String t = f.getType();
            if (t.contains("List<?>")) {
                t = "List<" + elemType + ">";
            } else if (t.equals("?")) {
                t = elemType;
            }
            clonedFields.add(new VariableDeclarationNode(f.getName(), t, f.getInitializer()));
        }

        StructNode clone = new StructNode(name + "_" + elemType, clonedFields);
        clone.setLLVMName(name + "_" + elemType);
        System.out.println(clone + " debug");
        return clone;
    }

    public int getLLVMSizeBytes() {
        int size = 0;
        for (VariableDeclarationNode field : fields) {
            size += llvmSizeOf(field.getType());
        }
        return size;
    }

    private int llvmSizeOf(String t) {
        switch (t) {
            case "int": return 4;
            case "double": return 8;
            case "float": return 4;
            case "boolean": return 1;
            case "string": return 8;
        }

        if (t.startsWith("List<")) return 8;

        if (t.startsWith("Struct")) return 8;

        return 8;
    }



}