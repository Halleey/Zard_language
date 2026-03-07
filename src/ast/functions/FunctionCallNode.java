package ast.functions;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;

import java.util.List;


public class FunctionCallNode extends ASTNode {
    private final String name;
    private final List<ASTNode> args;
    private Type type;
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
    public void bindChildren(StaticContext stx) {

        for (ASTNode arg : args) {
            arg.setParent(this);
            arg.bind(stx);
        }

        String[] parts = name.split("\\.");
        String funcName = parts[parts.length - 1];

        FunctionNode fn = stx.resolveFunction(funcName);

        if (fn == null) {
            throw new RuntimeException("Função não declarada: " + name);
        }

        this.type = fn.getReturnType();
    }

    @Override
    public Type getType() {
        return type;
    }

    public String getName() { return name; }
    public List<ASTNode> getArgs() { return args; }
}
