package ast.functions;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;


public class FunctionCallNode extends ASTNode {
    private final String name;
    private final List<ASTNode> args;

    public FunctionCallNode(String name, List<ASTNode> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        RuntimeContext currentCtx = ctx;

        String[] parts = name.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            String nsName = parts[i];
            TypedValue nsVal = currentCtx.getVariable(nsName);
            if (!nsVal.type().equals("namespace")) {
                throw new RuntimeException(nsName + " não é um namespace");
            }
            currentCtx = (RuntimeContext) nsVal.value();
        }

        String funcShortName = parts[parts.length - 1];
        TypedValue funcVal = currentCtx.getVariable(funcShortName);
        if (!funcVal.type().equals("function")) {
            throw new RuntimeException(funcShortName + " não é uma função");
        }

        FunctionNode func = (FunctionNode) funcVal.value();

        RuntimeContext localCtx = new RuntimeContext(currentCtx);
        for (int i = 0; i < func.getParams().size(); i++) {
            String paramName = func.getParams().get(i);
            TypedValue argVal = args.get(i).evaluate(ctx);

            String paramType = func.getParamTypes().get(i);
            argVal = promoteTypeIfNeeded(argVal, paramType);

            localCtx.declareVariable(paramName, argVal);
        }

        try {
            for (ASTNode node : func.getBody()) {
                node.evaluate(localCtx);
            }
        } catch (ReturnValue rv) {
            TypedValue retVal = rv.value;
            return promoteTypeIfNeeded(retVal, func.getReturnType());
        }

        return null;
    }

    private TypedValue promoteTypeIfNeeded(TypedValue value, String targetType) {
        if (value == null) return null;

        String valueType = value.type();

        if (valueType.equals(targetType)) return value;

        if (targetType.equals("double") && valueType.equals("int")) {
            return new TypedValue("double", ((Integer)value.value()).doubleValue());
        }

        if (targetType.startsWith("List<") && valueType.equals("List")) {
            return new TypedValue(targetType, value.value());
        }

        throw new RuntimeException("Não é possível converter " + valueType + " para " + targetType);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "FunctionCall " + name);

        if (!args.isEmpty()) {
            System.out.println(prefix + "  Args {");
            for (ASTNode arg : args) {
                arg.print(prefix + "    ");
            }
            System.out.println(prefix + "  }");
        } else {
            System.out.println(prefix + "  <no args>");
        }
    }


    public String getName() { return name; }
    public List<ASTNode> getArgs() { return args; }
}
