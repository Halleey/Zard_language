package ast.functions;

import ast.ASTNode;
import ast.context.runtime.RuntimeContext;
import ast.context.statics.StaticContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class FunctionReferenceNode extends ASTNode {
    private final String namespacePath; // ex: "Math.fatorial"

    public FunctionReferenceNode(String namespacePath) {
        this.namespacePath = namespacePath;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        // Resolve a função no contexto, incluindo namespaces
        RuntimeContext currentCtx = ctx;
        String[] parts = namespacePath.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            TypedValue nsVal = currentCtx.getVariable(parts[i]);
            if (!nsVal.type().equals("namespace")) {
                throw new RuntimeException(parts[i] + " não é um namespace");
            }
            currentCtx = (RuntimeContext) nsVal.value();
        }

        String funcName = parts[parts.length - 1];
        TypedValue funcVal = currentCtx.getVariable(funcName);
        if (!funcVal.type().equals("function")) {
            throw new RuntimeException(funcName + " não é uma função");
        }

        return funcVal; // retorna o FunctionNode encapsulado em TypedValue
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "FunctionReference: " + namespacePath);
    }

    @Override
    public void bind(StaticContext stx) {

    }
}
