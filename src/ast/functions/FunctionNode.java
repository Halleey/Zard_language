package ast.functions;

import ast.ASTNode;
import ast.expressions.ReferenceNode;
import ast.variables.TypeResolver;
import ast.variables.VariableNode;
import context.statics.StaticContext;
import context.statics.ScopeKind;
import ast.exceptions.ReturnValue;
import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;


import java.util.List;


public class FunctionNode extends ASTNode {

    private String name;
    private final List<ParamInfo> parameters;
    private final List<ASTNode> body;
    private Type returnType;                // já é Type
    private String implicitReceiverName;
    private Type implStructType;



    public Type getImplStructType() {
        return implStructType;
    }

    public void setImplicitReceiverName(String implicitReceiverName) {
        this.implicitReceiverName = implicitReceiverName;
    }


    public FunctionNode(String name,
                        List<ParamInfo> parameters,
                        List<ASTNode> body,
                        Type returnType) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType != null ? returnType : PrimitiveTypes.VOID;
        this.implStructType = null;
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

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type type) {
        this.returnType = type;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    @Override
    public List<ASTNode> getChildren() {
        return body;
    }

    @Override
    public void bindChildren(StaticContext stx) {
        StaticContext funcCtx = new StaticContext(ScopeKind.FUNCTION, stx);

        // receiver impl
        if (implicitReceiverName != null && implStructType != null) {
            funcCtx.declareVariable(implicitReceiverName, implStructType);
        }

        // parâmetros
        for (ParamInfo p : parameters) {
            funcCtx.declareVariable(p.name(), p.typeObj());
        }

        // corpo
        StaticContext bodyCtx = new StaticContext(ScopeKind.BLOCK, funcCtx);
        stx.declareFunction(this);

        for (ASTNode node : body) {
            node.bind(bodyCtx);
        }
    }

    @Override
    public LLVMValue accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ctx.declareVariable(name, new TypedValue(TypeResolver.resolve("function"), this));
        return TypedValue.VOID;
    }

    public TypedValue invoke(RuntimeContext parentCtx, List<ASTNode> argNodes) {
        RuntimeContext localCtx = new RuntimeContext(parentCtx);

        if (argNodes.size() < parameters.size()) {
            throw new RuntimeException("Argumentos insuficientes para função " + name);
        }

        for (int i = 0; i < parameters.size(); i++) {

            ParamInfo param = parameters.get(i);
            ASTNode argNode = argNodes.get(i);

            VariableNode varNode = getVariableNode(param, argNode);

            if (varNode != null) {
                //PASSAGEM POR REFERÊNCIA (slot)
                var slot = parentCtx.getSlot(varNode.getName());
                localCtx.bindSlot(param.name(), slot);

            } else {
                //PASSAGEM POR VALOR (cópia)
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

        return TypedValue.VOID;
    }

    private static VariableNode getVariableNode(ParamInfo param, ASTNode argNode) {
        boolean paramIsRef = param.isRef();
        boolean argIsRef = argNode instanceof ReferenceNode;

        VariableNode varNode = null;

        //CASO ReferenceNode explícito (&x)
        if (argIsRef) {
            varNode = (VariableNode) ((ReferenceNode) argNode).getTarget();
        }

        //CASO: parâmetro exige referência → auto-ref
        else if (paramIsRef) {

            if (!(argNode instanceof VariableNode v)) {
                throw new RuntimeException(
                        "Parâmetro '&" + param.name() + "' exige variável"
                );
            }

            varNode = v;
        }
        return varNode;
    }

    @Override
    public void print(String prefix) {
        StringBuilder sig = new StringBuilder();
        sig.append(prefix).append("Function ").append(name).append("(");

        for (int i = 0; i < parameters.size(); i++) {
            ParamInfo p = parameters.get(i);
            if (p.isRef()) sig.append("&");
            sig.append(p.name()).append(": ").append(p.typeObj().name());
            if (i < parameters.size() - 1) sig.append(", ");
        }

        sig.append(") -> ").append(returnType.name());
        System.out.println(sig);

        if (!body.isEmpty()) {
            System.out.println(prefix + "  Body ");
            for (ASTNode stmt : body) stmt.print(prefix + "    ");
        }
    }
}