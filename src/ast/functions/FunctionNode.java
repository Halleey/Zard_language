package ast.functions;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;


public class FunctionNode extends ASTNode {
    private final String name;
    private final List<String> params;
    private final List<String> paramTypes;
    private final List<ASTNode> body;
    private final String returnType; // novo campo

    public FunctionNode(String name, List<String> params, List<String> paramTypes,
                        List<ASTNode> body, String returnType) {
        this.name = name;
        this.params = params;
        this.paramTypes = paramTypes;
        this.body = body;
        this.returnType = returnType != null ? returnType : "void"; // default
    }

    public List<String> getParamTypes() { return paramTypes; }
    public String getReturnType() { return returnType; }
    public String getName() { return name; }
    public List<String> getParams() { return params; }
    public List<ASTNode> getBody() { return body; }

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
        System.out.println(prefix + "Function: " + name + " returns " + returnType);
        if (!params.isEmpty()) {
            System.out.println(prefix + "  Parameters:");
            for (int i = 0; i < params.size(); i++) {
                System.out.println(prefix + "    " + params.get(i) + " type " + paramTypes.get(i));
            }
        }
        if (!body.isEmpty()) {
            System.out.println(prefix + "  Body:");
            for (ASTNode stmt : body) {
                stmt.print(prefix + "    ");
            }
        }
    }
}
