package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.List;
public class FunctionEmitter {
    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper = new TypeMapper();
    private final ReturnTypeInferer returnInferer;

    public FunctionEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
        this.returnInferer = new ReturnTypeInferer(visitor, typeMapper);
    }

    public String emit(FunctionNode fn) {
        StringBuilder sb = new StringBuilder();

        // registra função inicialmente com tipo provisório "any"
        visitor.registerFunctionType(fn.getName(), "any");

        // deduz tipo de retorno se necessário
        String llvmRetType = fn.getReturnType();
        if ("void".equals(llvmRetType) && containsReturn(fn)) {
            visitor.getCallEmitter().markBeingDeduced(fn.getName());
            llvmRetType = returnInferer.deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(fn.getName());
        }
        llvmRetType = typeMapper.toLLVM(llvmRetType);
        visitor.registerFunctionType(fn.getName(), llvmRetType);

        // assinatura LLVM
        List<String> paramSignatures = new ArrayList<>();
        for (int i = 0; i < fn.getParams().size(); i++) {
            String llvmType = typeMapper.toLLVM(fn.getParamTypes().get(i));
            paramSignatures.add(llvmType + " %" + fn.getParams().get(i));
        }

        sb.append("; === Função: ").append(fn.getName()).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(fn.getName())
                .append("(").append(String.join(", ", paramSignatures)).append(") {\nentry:\n");

        // Aloca e registra parâmetros na stack (somente manual, sem ParamEmitter)
        for (int i = 0; i < fn.getParams().size(); i++) {
            String paramName = fn.getParams().get(i);
            String paramType = typeMapper.toLLVM(fn.getParamTypes().get(i));
            String paramPtr = "%" + paramName + "_addr";

            sb.append("  ").append(paramPtr).append(" = alloca ").append(paramType).append("\n");
            sb.append("  store ").append(paramType).append(" %").append(paramName)
                    .append(", ").append(paramType).append("* ").append(paramPtr).append("\n")
                    .append(";;VAL:").append(paramPtr).append(";;TYPE:").append(paramType).append("\n");

            visitor.getVariableEmitter().registerVarPtr(paramName, paramPtr);
            visitor.putVarType(paramName, paramType);
        }

        // Corpo da função
        for (ASTNode stmt : fn.getBody()) {
            sb.append(stmt.accept(visitor));
        }

        // Garante return void se necessário
        if ("void".equals(llvmRetType) && !containsReturn(fn)) {
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }

    private boolean containsReturn(FunctionNode fn) {
        return fn.getBody().stream().anyMatch(n -> n instanceof ReturnNode);
    }
}
