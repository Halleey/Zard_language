package ast.functions;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;
public class FunctionNode extends ASTNode {
    public final String name;
    public final List<String> params;
    public final List<String> paramTypes; // novo
    public final List<ASTNode> body;

    public FunctionNode(String name, List<String> params, List<String> paramTypes, List<ASTNode> body) {
        this.name = name;
        this.params = params;
        this.paramTypes = paramTypes; // inicializa
        this.body = body;
    }

    public List<String> getParamTypes() { return paramTypes; }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
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
                System.out.println(prefix + "    " + p + " type " + getParamTypes());
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
