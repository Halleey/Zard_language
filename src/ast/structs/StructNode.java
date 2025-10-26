package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.runtime.RuntimeContext;
import ast.variables.VariableDeclarationNode;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class StructNode extends ASTNode {
    private final String name;
    private final List<VariableDeclarationNode> fields;
    public String getName() {
        return name;
    }

    public List<VariableDeclarationNode> getFields() {
        return fields;
    }

    public StructNode(String name, List<VariableDeclarationNode> fields) {
        this.name = name;
        this.fields = fields;
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


}
