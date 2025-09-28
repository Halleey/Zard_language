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

        // registra com tipo temporario "any" para permitir recursão
        visitor.registerFunctionType(fn.getName(), "any");

        // registra tipos dos parametros na tabela
        for (int i = 0; i < fn.getParams().size(); i++) {
            visitor.putVarType(fn.getParams().get(i), fn.getParamTypes().get(i));
        }

        // deduz retorno se necessário
        String llvmRetType = fn.getReturnType();
        if ("void".equals(llvmRetType) && containsReturn(fn)) {
            visitor.getCallEmitter().markBeingDeduced(fn.getName());
            llvmRetType = returnInferer.deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(fn.getName());
        }
        llvmRetType = typeMapper.toLLVM(llvmRetType);

        // atualiza função com tipo correto
        visitor.registerFunctionType(fn.getName(), llvmRetType);

        // gera assinatura
        List<String> params = new ArrayList<>();
        for (int i = 0; i < fn.getParams().size(); i++) {
            params.add(typeMapper.toLLVM(fn.getParamTypes().get(i)) + " %" + fn.getParams().get(i));
        }
        sb.append("; === Função: ").append(fn.getName()).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(fn.getName())
                .append("(").append(String.join(", ", params)).append(") {\nentry:\n");

        // parametros
        ParamEmitter paramEmitter = new ParamEmitter(visitor);
        sb.append(paramEmitter.emitParams(fn));

        // corpo
        for (ASTNode stmt : fn.getBody()) {
            sb.append(stmt.accept(visitor));
        }

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
