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
        TypedValue funcVal = ctx.getVariable(name);
        if (!funcVal.getType().equals("function")) {
            throw new RuntimeException(name + " não é uma função");
        }

        FunctionNode func = (FunctionNode) funcVal.getValue();

        // Cria um novo contexto para a função (escopo local)
        RuntimeContext localCtx = new RuntimeContext();

        // Passa os argumentos
        for (int i = 0; i < func.params.size(); i++) {
            String paramName = func.params.get(i);   // agora é apenas "x" ou "z"
            TypedValue argVal = args.get(i).evaluate(ctx); // avalia no contexto atual
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

    }

    public String getName() {
        return name;
    }

    public List<ASTNode> getArgs() {
        return args;
    }

}