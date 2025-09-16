package ast.functions;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

import java.util.List;

public class FunctionNode extends ASTNode {
    public final String name;
    public final List<String> params;
    public final List<ASTNode> body;

    public FunctionNode(String name, List<String> params, List<ASTNode> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ctx.declareVariable(name, new TypedValue("function", this));
        return null;
    }

    public String getName() {
        return name;
    }

    public List<String> getParams() {
        return params;
    }

    public List<ASTNode> getBody() {
        return body;
    }
}
