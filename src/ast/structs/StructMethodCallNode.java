package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.functions.FunctionNode;
import ast.runtime.RuntimeContext;
import ast.variables.VariableNode;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;
public class StructMethodCallNode extends ASTNode {

    private final ASTNode structInstance;
    private final String structName;
    private final String methodName;
    private final List<ASTNode> args;

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

        TypedValue instanceVal = structInstance.evaluate(ctx);

        String base = structName.contains("<")
                ? structName.substring(0, structName.indexOf('<'))
                : structName;

        FunctionNode method = ctx.getStructMethod(base, methodName);
        if (method == null)
            throw new RuntimeException("Method " + methodName + " not found in Struct " + structName);

        List<TypedValue> evaluatedArgs = new ArrayList<>();
        for (ASTNode arg : args) {
            evaluatedArgs.add(arg.evaluate(ctx));
        }

        if (!method.getParamTypes().isEmpty()) {
            String first = method.getParamTypes().get(0);
            if (first.startsWith("Struct<" + base + ">")) {
                evaluatedArgs.add(0, instanceVal);
            }
        }

        RuntimeContext local = new RuntimeContext(ctx);
        local.declareVariable("s", instanceVal);

        return method.invoke(local, evaluatedArgs);
    }

    public String getReceiverName() {
        if (structInstance instanceof VariableNode v) {
            return v.getName();
        }
        return null;
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
}
