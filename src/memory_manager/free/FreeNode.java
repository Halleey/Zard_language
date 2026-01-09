package memory_manager.free;

import ast.ASTNode;
import ast.expressions.TypedValue;
import context.runtime.RuntimeContext;
import low.module.LLVMEmitVisitor;
import memory_manager.ownership.graphs.OwnershipNode;

import java.util.Collections;
import java.util.List;
public class FreeNode extends ASTNode {

    private final OwnershipNode root;

    public FreeNode(OwnershipNode root) {
        this.root = root;
    }

    public OwnershipNode getRoot() {
        return root;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visitFreeNode(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return TypedValue.VOID;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Free:");
        System.out.println(prefix + "  " + root.getSymbol().getName());
    }

    @Override
    public List<ASTNode> getChildren() {
        return Collections.emptyList();
    }
}
