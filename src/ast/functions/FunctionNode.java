package ast.functions;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;


public class FunctionNode extends ASTNode {
    private String name;

    public void setParams(List<String> params) {
        this.params = params;
    }

    private  List<String> params;
    private  List<String> paramTypes;
    private final List<ASTNode> body;
    private  String returnType;
    private String implicitReceiverName;

    public String getImplicitReceiverName() {
        return implicitReceiverName;
    }

    public void setImplicitReceiverName(String implicitReceiverName) {
        this.implicitReceiverName = implicitReceiverName;
    }
    public void setName(String newName) {
        this.name = newName;
    }

    public void setParamTypes(List<String> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public void setReturnType(String type) { this.returnType = type; }

    @Override
    public List<ASTNode> getChildren() { return body; }


    private String implStructName; // null se não for função de impl

    public FunctionNode(String name, List<String> params, List<String> paramTypes,
                        List<ASTNode> body, String returnType) {
        this.name = name;
        this.params = params;
        this.paramTypes = paramTypes;
        this.body = body;
        this.returnType = returnType != null ? returnType : "void";
        this.implStructName = null;
    }

    public void setImplStructName(String structName) {
        this.implStructName = structName;
    }

    public String getImplStructName() {
        return implStructName;
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

    public TypedValue invoke(RuntimeContext parentCtx, List<TypedValue> args) {
        RuntimeContext localCtx = new RuntimeContext(parentCtx);

        for (int i = 0; i < params.size(); i++) {
            String paramName = params.get(i);
            TypedValue argValue = i < args.size() ? args.get(i) : new TypedValue("void", null);
            String paramType = i < paramTypes.size() ? paramTypes.get(i) : "any";

            argValue = promoteTypeIfNeeded(argValue, paramType);
            localCtx.declareVariable(paramName, argValue);
        }

        try {
            for (ASTNode node : body) {
                node.evaluate(localCtx);
            }
        } catch (ReturnValue rv) {
            TypedValue retVal = rv.value;
            return promoteTypeIfNeeded(retVal, returnType);
        }

        return null;
    }

    private TypedValue promoteTypeIfNeeded(TypedValue value, String targetType) {
        if (value == null) return null;
        String valueType = value.type();

        if (valueType.equals(targetType)) return value;

        if (targetType.equals("double") && valueType.equals("int")) {
            return new TypedValue("double", ((Integer) value.value()).doubleValue());
        }

        if (targetType.startsWith("List<") && valueType.equals("List")) {
            return new TypedValue(targetType, value.value());
        }

        return value;
    }

    @Override
    public void print(String prefix) {
        StringBuilder sig = new StringBuilder();
        sig.append(prefix).append("Function ").append(name).append("(");
        for (int i = 0; i < params.size(); i++) {
            sig.append(params.get(i)).append(": ").append(paramTypes.get(i));
            if (i < params.size() - 1) sig.append(", ");
        }
        sig.append(") -> ").append(returnType);
        System.out.println(sig);

        if (!body.isEmpty()) {
            System.out.println(prefix + "  Body ");
            for (ASTNode stmt : body) stmt.print(prefix + "    ");
        }
    }
}
