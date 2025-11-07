package ast.structs;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.functions.FunctionNode;
import ast.runtime.RuntimeContext;
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

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue instanceVal = structInstance.evaluate(ctx);

        FunctionNode method = ctx.getStructMethod(structName, methodName);
        if (method == null) {
            throw new RuntimeException("Método " + methodName + " não encontrado em Struct " + structName);
        }

        List<TypedValue> argValues = new ArrayList<>();
        for (ASTNode arg : args) {
            argValues.add(arg.evaluate(ctx));
        }

        RuntimeContext local = new RuntimeContext(ctx);

        return method.invoke(local, argValues);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "StructMethodCall:");
        System.out.println(prefix + "  Struct: " + structName);
        System.out.println(prefix + "  Method: " + methodName);
        if (!args.isEmpty()) {
            System.out.println(prefix + "  Args:");
            for (ASTNode arg : args) {
                arg.print(prefix + "    ");
            }
        }
    }
}

