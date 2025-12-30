package ast.structs;

import ast.ASTNode;
import ast.context.statics.StaticContext;
import ast.expressions.TypedValue;
import ast.functions.FunctionNode;
import ast.context.runtime.RuntimeContext;
import low.module.LLVMEmitVisitor;

import java.util.List;
import java.util.Map;

public class ImplNode extends ASTNode {
    private final String structName;
    private final List<FunctionNode> methods;

    public ImplNode(String structName, List<FunctionNode> methods) {
        this.structName = structName;
        this.methods = methods;
    }

    public String getStructName() {
        return structName;
    }

    public List<FunctionNode> getMethods() {
        return methods;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        Map<String, FunctionNode> map = ctx.getOrCreateStructMethodTable(structName);
        for (FunctionNode fn : methods) {
            map.put(fn.getName(), fn);
        }
        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + " Impl for  Struct<" + structName + ">");
        for (FunctionNode fn : methods) {
            fn.print(prefix + " ");
        }
    }

    @Override
    public void bind(StaticContext stx) {

    }
}
