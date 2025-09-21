package ast.functions;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;

public class FunctionCallNode extends ASTNode {
    private final String name; // ex: "dobrar" ou "math.dobrar"
    private final List<ASTNode> args;

    public FunctionCallNode(String name, List<ASTNode> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        RuntimeContext currentCtx = ctx;

        // Divide a chamada em namespaces e função
        String[] parts = name.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            String nsName = parts[i];
            TypedValue nsVal = currentCtx.getVariable(nsName);
            if (!nsVal.getType().equals("namespace")) {
                throw new RuntimeException(nsName + " não é um namespace");
            }
            currentCtx = (RuntimeContext) nsVal.getValue(); // desce para o contexto do namespace
        }

        String funcShortName = parts[parts.length - 1];
        TypedValue funcVal = currentCtx.getVariable(funcShortName);
        if (!funcVal.getType().equals("function")) {
            throw new RuntimeException(funcShortName + " não é uma função");
        }

        FunctionNode func = (FunctionNode) funcVal.getValue();

        // Cria contexto local filho baseado no namespace/função
        RuntimeContext localCtx = new RuntimeContext(currentCtx);

        // Avalia os argumentos no contexto chamador e declara no localCtx
        for (int i = 0; i < func.params.size(); i++) {
            String paramName = func.params.get(i);
            TypedValue argVal = args.get(i).evaluate(ctx); // contexto chamador
            localCtx.declareVariable(paramName, argVal);
        }

        // Executa o corpo da função
        try {
            for (ASTNode node : func.body) {
                node.evaluate(localCtx);
            }
        } catch (ReturnValue rv) {
            return rv.value;
        }

        return null; // se não houver return
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "FunctionCall: " + name);
        if (!args.isEmpty()) {
            System.out.println(prefix + "  Args:");
            for (ASTNode arg : args) {
                arg.print(prefix + "    ");
            }
        }
    }

    public String getName() { return name; }
    public List<ASTNode> getArgs() { return args; }
}
