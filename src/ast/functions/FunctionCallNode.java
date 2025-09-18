package ast.functions;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

import java.util.List;

public class FunctionCallNode extends ASTNode {
    private final String name;
    private final List<ASTNode> args;

    public FunctionCallNode(String name, List<ASTNode> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        // Pega a função do contexto atual
        TypedValue funcVal = ctx.getVariable(name);
        if (!funcVal.getType().equals("function")) {
            throw new RuntimeException(name + " não é uma função");
        }

        FunctionNode func = (FunctionNode) funcVal.getValue();

        // Cria um contexto filho baseado no contexto atual
        RuntimeContext localCtx = new RuntimeContext(ctx); // <- aqui o construtor precisa aceitar contexto pai

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
