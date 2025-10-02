package low.functions;

import ast.functions.FunctionNode;
import ast.variables.VariableDeclarationNode;
import low.module.LLVisitorMain;
class ParamEmitter {
    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper = new TypeMapper();

    public ParamEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public String emitParams(FunctionNode fn) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fn.getParams().size(); i++) {
            String name = fn.getParams().get(i);
            String type = fn.getParamTypes().get(i);

            String llvmType = typeMapper.toLLVM(type);

            // nome do ponteiro na stack
            String ptrName = "%" + name + "_addr";

            // aloca espaço na stack
            sb.append("  ").append(ptrName).append(" = alloca ").append(llvmType).append("\n");

            // store do valor recebido no ponteiro
            sb.append("  store ").append(llvmType).append(" %").append(name)
                    .append(", ").append(llvmType).append("* ").append(ptrName).append("\n");

            // gera load imediato para permitir BinaryOp e Print
            String tmpLoad = visitor.getTemps().newTemp();
            sb.append("  ").append(tmpLoad).append(" = load ").append(llvmType)
                    .append(", ").append(llvmType).append("* ").append(ptrName).append("\n")
                    .append(";;VAL:").append(tmpLoad).append(";;TYPE:").append(llvmType).append("\n");
            System.out.println("debugando" + tmpLoad);
            // registra ponteiro na tabela de variáveis
            visitor.varEmitter.registerVarPtr(name, ptrName);
        }

        return sb.toString();
    }
}
