package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.variables.LiteralNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.List;


import ast.*;


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

        // nome da função LLVM
        String baseName = fn.getName();

// obtemos o tipo do struct que implementa a função, se existir
        Type implStructType = fn.getImplStructType();
        String implOwner = implStructType != null ? implStructType.name() : null;

        String irName = (implOwner != null && !implOwner.isEmpty())
                ? implOwner + "_" + baseName
                : baseName;

        visitor.functions.put(irName, fn);

        visitor.registerFunctionType(irName, new TypeInfos(PrimitiveTypes.ANY, "void"));

        Type declaredType = fn.getReturnType();
        TypeInfos retInfo;

        if (declaredType instanceof PrimitiveTypes p && "void".equals(p.name()) && containsReturn(fn)) {
            visitor.getCallEmitter().markBeingDeduced(baseName);
            retInfo = returnInferer.deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(baseName);
        } else {
            String llvmType = typeMapper.toLLVM(declaredType);
            retInfo = new TypeInfos(declaredType, llvmType);
        }

// registra o tipo de retorno definitivo
        visitor.registerFunctionType(irName, retInfo);
        String llvmRetType = retInfo.getLLVMType();

// Preparar parâmetros
        List<String> paramSignatures = new ArrayList<>();
        for (ParamInfo p : fn.getParameters()) {
            Type paramType = p.typeObj();           // agora é Type real
            String llvmType = typeMapper.toLLVM(paramType); // LLVM type

            visitor.putVarType(p.name(), new TypeInfos(paramType, llvmType));

            // parâmetro por referência
            String llvmInSignature = p.isRef() ? llvmType + "*" : llvmType;
            paramSignatures.add(llvmInSignature + " %" + p.name());
        }

// iniciar corpo da função LLVM
        sb.append("; === Função: ").append(irName).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(irName)
                .append("(").append(String.join(", ", paramSignatures)).append(") {\nentry:\n");

        // Alocação de parâmetros
        for (ParamInfo p : fn.getParameters()) {
            String paramName = p.name();
            TypeInfos info = visitor.getVarType(paramName);
            String valueLLVM = info.getLLVMType();
            String paramPtrName = "%" + paramName + "_addr";

            if (p.isRef()) {
                // variável já é referência → ponteiro direto
                visitor.getVariableEmitter().registerVarPtr(paramName, "%" + paramName);
            } else {
                // aloca espaço para variável e armazena o valor inicial
                sb.append("  ").append(paramPtrName)
                        .append(" = alloca ").append(valueLLVM).append("\n");
                sb.append("  store ").append(valueLLVM)
                        .append(" %").append(paramName)
                        .append(", ").append(valueLLVM).append("* ").append(paramPtrName).append("\n");

                visitor.getVariableEmitter().registerVarPtr(paramName, paramPtrName);
            }
        }

        // Emitir corpo da função
        for (ASTNode stmt : fn.getBody()) {
            sb.append(stmt.accept(visitor));
        }

        // Retorno implícito para void
        if (llvmRetType.equals("void") && !containsReturn(fn)) {
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }


    private boolean containsReturn(FunctionNode fn) {
        for (ASTNode node : fn.getBody()) {
            if (node instanceof ReturnNode) return true;
        }
        return false;
    }

    private String normalizeSourceType(String t) {
        if (t == null) return "void";
        t = t.trim();
        if (t.startsWith("Struct<") && t.endsWith(">")) {
            return t.substring(7, t.length() - 1).trim();
        }
        return t;
    }

//    private boolean containsReturn(FunctionNode fn) {
//        boolean has = fn.getBody().stream().anyMatch(n -> n instanceof ReturnNode);
//        System.out.println("[FEmit] containsReturn(" + fn.getName() + ") = " + has);
//        return has;
//    }
}
