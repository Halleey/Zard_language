package ast.structs;

import ast.ASTNode;
import ast.context.StaticContext;
import ast.expressions.TypedValue;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.context.RuntimeContext;
import ast.variables.VariableNode;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;


public class StructMethodCallNode extends ASTNode {

    private final ASTNode structInstance;
    private final String structName;
    private final String methodName;
    private final List<ASTNode> args;
    private String returnType;
    public StructMethodCallNode(ASTNode structInstance, String structName, String methodName, List<ASTNode> args) {
        this.structInstance = structInstance;
        this.structName = structName;
        this.methodName = methodName;
        this.args = args;
    }

    public ASTNode getStructInstance() { return structInstance; }
    public String getStructName() { return structName; }
    public String getMethodName() { return methodName; }
    public List<ASTNode> getArgs() { return args; }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        ASTNode instanceExpr = structInstance;

        String base = structName.contains("<")
                ? structName.substring(0, structName.indexOf('<'))
                : structName;

        FunctionNode method = ctx.getStructMethod(base, methodName);
        if (method == null) {
            throw new RuntimeException(
                    "Method " + methodName + " not found in Struct " + structName
            );
        }

        List<ASTNode> callArgs = new ArrayList<>();
        List<ParamInfo> params = method.getParameters();

        if (!params.isEmpty()) {
            String firstType = params.get(0).type();
            if (firstType != null && firstType.startsWith("Struct<" + base + ">")) {
                callArgs.add(instanceExpr);
            }
        }

        if (args != null) {
            callArgs.addAll(args);
        }

        return method.invoke(ctx, callArgs);
    }


    public String getReceiverName() {
        if (structInstance instanceof VariableNode v) {
            return v.getName();
        }
        return null;
    }
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnType() {
        return returnType;
    }
    @Override
    public void print(String prefix) {
        System.out.println(prefix + "StructMethodCall:");
        System.out.println(prefix + "  Struct: " + structName);
        System.out.println(prefix + "  Method: " + methodName);
        if (!args.isEmpty()) {
            System.out.println(prefix + "  Args:");
            for (ASTNode a : args) a.print(prefix + "    ");
        }
    }

    @Override
    public void bind(StaticContext stx) {

    }
}
