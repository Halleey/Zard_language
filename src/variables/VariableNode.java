package variables;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

public class VariableNode extends ASTNode {
    public final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return ctx.getVariable(name);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Variable: " + name);
    }

}
