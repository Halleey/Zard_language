package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.runtime.RuntimeContext;
import ast.variables.VariableDeclarationNode;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.List;

public class StructNode extends ASTNode {
    private final String name;
    private final List<VariableDeclarationNode> fields;
    private String llvmName; // nome LLVM único, ex: Set_int, Set_double

    public StructNode(String name, List<VariableDeclarationNode> fields) {
        this.name = name;
        this.fields = fields;
        this.llvmName = name;
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
        return clone;
    }
}