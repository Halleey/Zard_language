package ast.functions;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

import java.util.List;
public class FunctionCallNode extends ASTNode {
    private final String name; // pode ser "dobrar" ou "math.dobrar no contexto de imports (exemplo)"
    private final List<ASTNode> args;

    public FunctionCallNode(String name, List<ASTNode> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        RuntimeContext currentCtx = ctx;
        String[] parts = name.split("\\."); // divide namespace e função
        for (int i = 0; i < parts.length - 1; i++) {
            String nsName = parts[i];
            TypedValue nsVal = currentCtx.getVariable(nsName);
            if (!nsVal.getType().equals("namespace")) {
                throw new RuntimeException(nsName + " não é um namespace");
            }
            currentCtx = (RuntimeContext) nsVal.getValue(); // navega para o contexto do namespace
        }

        String funcShortName = parts[parts.length - 1];
        TypedValue funcVal = currentCtx.getVariable(funcShortName);
        if (!funcVal.getType().equals("function")) {
            throw new RuntimeException(funcShortName + " não é uma função");
        }

        FunctionNode func = (FunctionNode) funcVal.getValue();

        // Cria um contexto filho baseado no contexto do namespace/função
        RuntimeContext localCtx = new RuntimeContext(currentCtx);

        // Mapeia os argumentos para os parâmetros
        for (int i = 0; i < func.params.size(); i++) {
            String paramName = func.params.get(i);
            TypedValue argVal = args.get(i).evaluate(ctx); // avalia no contexto chamador
            localCtx.declareVariable(paramName, argVal);
        }

        // Executa o corpo da função
        try {
            for (ASTNode node : func.body) {
                node.evaluate(localCtx);
            }
        } catch (ReturnValue rv) {
            return rv.value; // retorna se houver return
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

    public String getName() {
        return name;
    }

    public List<ASTNode> getArgs() {
        return args;
    }
}
