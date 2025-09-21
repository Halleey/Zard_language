package ast.functions;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import low.module.LLVMEmitVisitor;

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
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ctx.declareVariable(name, new TypedValue("function", this));
        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Function: " + name);
        if (!params.isEmpty()) {
            System.out.println(prefix + "  Parameters:");
            for (String p : params) {
                System.out.println(prefix + "    " + p);
            }
        }
        if (!body.isEmpty()) {
            System.out.println(prefix + "  Body:");
            for (ASTNode stmt : body) {
                stmt.print(prefix + "    ");
            }
        }
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
