package low.functions;

import ast.functions.FunctionNode;
import ast.variables.VariableDeclarationNode;
import low.module.LLVisitorMain;

class ParamEmitter {
    private final LLVisitorMain visitor;

    public ParamEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public String emitParams(FunctionNode fn) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fn.getParams().size(); i++) {
            String name = fn.getParams().get(i);
            String type = fn.getParamTypes().get(i);

            String ptrName = "%" + name + ".addr";
            VariableDeclarationNode paramNode = new VariableDeclarationNode(ptrName, type, null);
            sb.append(visitor.varEmitter.emitAlloca(paramNode));
            sb.append("  store ").append(new TypeMapper().toLLVM(type))
                    .append(" %").append(name).append(", ")
                    .append(new TypeMapper().toLLVM(type)).append("* ").append(ptrName).append("\n");

            visitor.varEmitter.registerVarPtr(name, ptrName);
        }
        return sb.toString();
    }
}
