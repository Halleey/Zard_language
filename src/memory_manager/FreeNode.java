package memory_manager;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.runtime.RuntimeContext;
import low.module.LLVMEmitVisitor;

import java.util.List;


public class FreeNode extends ASTNode {

    private final String varName;

    public FreeNode(String varName) {
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ctx.remove(varName);
        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Free: " + varName);
    }

    @Override
    public List<ASTNode> getChildren() {
        return List.of();
    }
}
