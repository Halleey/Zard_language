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

    public ASTNode getStructInstance() {
        return structInstance;
    }

    public String getStructName() {
        return structName;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<ASTNode> getArgs() {
        return args;
    }

    public StructMethodCallNode(ASTNode structInstance, String structName, String methodName, List<ASTNode> args) {
        this.structInstance = structInstance;
        this.structName = structName;
        this.methodName = methodName;
        this.args = args;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue instanceVal = structInstance.evaluate(ctx);

        FunctionNode method = ctx.getStructMethod(structName, methodName);
        if (method == null) {
            throw new RuntimeException("Method " + methodName + " not found in Struct " + structName);
        }

        List<TypedValue> argValues = new ArrayList<>();
        for (ASTNode arg : args) {
            argValues.add(arg.evaluate(ctx));
        }

        if (!method.getParamTypes().isEmpty()) {
            String firstType = method.getParamTypes().get(0);
            if (firstType.startsWith("Struct<" + structName + ">")) {
                argValues.add(0, instanceVal);
            }
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

    public String getReceiverName() {
        if (structInstance instanceof VariableNode v) {
            return v.getName(); //gambiarra basica para extrair o real nome da estrutura
        }
        return null;
    }


}

