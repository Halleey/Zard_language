package ast.functions;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;


public class FunctionCallNode extends ASTNode {
    private final String name;
    private final List<ASTNode> args;

    public FunctionCallNode(String name, List<ASTNode> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        RuntimeContext currentCtx = ctx;

        // permite chamadas como "math.pow" navegando por namespaces
        String[] parts = name.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            String nsName = parts[i];
            TypedValue nsVal = currentCtx.getVariable(nsName);

            if (!nsVal.isNamespace()) {
                throw new RuntimeException(nsName + " não é um namespace");
            }

            currentCtx = (RuntimeContext) nsVal.value();
        }

        String funcShortName = parts[parts.length - 1];
        TypedValue funcVal = currentCtx.getVariable(funcShortName);

        if (!funcVal.isFunction()) {
            throw new RuntimeException(funcShortName + " não é uma função");
        }

        FunctionNode func = funcVal.getFunction();
        return func.invoke(currentCtx, args);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "FunctionCall " + name);
        if (!args.isEmpty()) {
            System.out.println(prefix + "  Args ");
            for (ASTNode arg : args) arg.print(prefix + "    ");
        } else {
            System.out.println(prefix + "  <no args>");
        }
    }

    @Override
    public void bind(StaticContext stx) {

    }

    public String getName() { return name; }
    public List<ASTNode> getArgs() { return args; }
}
