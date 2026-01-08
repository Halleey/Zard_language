package ast.functions;

import ast.ASTNode;
import context.statics.StaticContext;
import context.statics.ScopeKind;
import ast.exceptions.ReturnValue;
import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;


public class FunctionNode extends ASTNode {
    private String name;
    private final List<ParamInfo> parameters;
    private final List<ASTNode> body;
    private String returnType;
    private String implicitReceiverName; // "s" em métodos de impl
    private String implStructName;       // null se não for método de impl

    public FunctionNode(String name,
                        List<ParamInfo> parameters,
                        List<ASTNode> body,
                        String returnType) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = (returnType != null && !returnType.isBlank())
                ? returnType
                : "void";
        this.implStructName = null;
    }



    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public List<ParamInfo> getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String type) {
        this.returnType = type;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    public void setImplicitReceiverName(String implicitReceiverName) {
        this.implicitReceiverName = implicitReceiverName;
    }

    public String getImplStructName() {
        return implStructName;
    }

    @Override
    public List<ASTNode> getChildren() {
        return body;
    }
    @Override
    public void bindChildren(StaticContext stx) {

        StaticContext funcCtx =
                new StaticContext(ScopeKind.FUNCTION, stx);

        if (implicitReceiverName != null && implStructName != null) {
            funcCtx.declareVariable(
                    implicitReceiverName,
                    implStructName
            );
        }

        if (parameters != null) {
            for (ParamInfo p : parameters) {
                funcCtx.declareVariable(p.name(), p.type());
            }
        }
        StaticContext bodyCtx =
                new StaticContext(ScopeKind.BLOCK, funcCtx);

        for (ASTNode node : body) {
            node.bind(bodyCtx);
        }
    }


    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }


    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ctx.declareVariable(name, new TypedValue("function", this));
        return null;
    }

    public TypedValue invoke(RuntimeContext parentCtx, List<ASTNode> argNodes) {
        RuntimeContext localCtx = new RuntimeContext(parentCtx);

        if (argNodes.size() < parameters.size()) {
            throw new RuntimeException("Argumentos insuficientes para função " + name);
        }

        for (int i = 0; i < parameters.size(); i++) {
            ParamInfo param = parameters.get(i);
            ASTNode argNode = argNodes.get(i);

            if (param.isRef()) {
                if (!(argNode instanceof ast.variables.VariableNode var)) {
                    throw new RuntimeException(
                            "Parâmetro por referência '&" + param.name() + "' exige uma variável, não uma expressão"
                    );
                }

                var slot = parentCtx.getSlot(var.getName());
                localCtx.bindSlot(param.name(), slot);

            } else {

                TypedValue value = argNode.evaluate(parentCtx);

                localCtx.declareVariable(param.name(), value);
            }
        }

        try {
            for (ASTNode node : body) {
                node.evaluate(localCtx);
            }
        } catch (ReturnValue rv) {

            return rv.value;
        }

        return null;
    }

    @Override
    public void print(String prefix) {
        StringBuilder sig = new StringBuilder();
        sig.append(prefix).append("Function ").append(name).append("(");

        for (int i = 0; i < parameters.size(); i++) {
            ParamInfo p = parameters.get(i);
            if (p.isRef()) {
                sig.append("&");
            }
            sig.append(p.name())
                    .append(": ")
                    .append(p.type());
            if (i < parameters.size() - 1) sig.append(", ");
        }

        sig.append(") -> ").append(returnType);
        System.out.println(sig);

        if (!body.isEmpty()) {
            System.out.println(prefix + "  Body ");
            for (ASTNode stmt : body) stmt.print(prefix + "    ");
        }
    }




}
