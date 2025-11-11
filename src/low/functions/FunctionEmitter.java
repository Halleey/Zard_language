package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.variables.LiteralNode;
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

        String baseName = fn.getName();
        String implOwner = fn.getImplStructName(); // Ex: Set_int ou Set_double
        String irName = (implOwner != null && !implOwner.isEmpty())
                ? implOwner + "_" + baseName
                : baseName;

        visitor.registerFunctionType(irName, new TypeInfos("any", "void", null));

        String declaredType = normalizeSourceType(fn.getReturnType());
        TypeInfos retInfo;
        if ("void".equals(declaredType) && containsReturn(fn)) {
            visitor.getCallEmitter().markBeingDeduced(baseName);
            retInfo = returnInferer.deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(baseName);
        } else {
            String normalized = normalizeSourceType(declaredType);
            String llvm = typeMapper.toLLVM(normalized);
            String elem = null;
            if (normalized.startsWith("List<") && normalized.endsWith(">")) {
                elem = normalized.substring(5, normalized.length() - 1);
            }
            retInfo = new TypeInfos(normalized, llvm, elem);
        }

        visitor.registerFunctionType(irName, retInfo);
        String llvmRetType = retInfo.getLLVMType();

        List<String> paramSignatures = new ArrayList<>();
        for (int i = 0; i < fn.getParams().size(); i++) {
            String rawParamType = fn.getParamTypes().get(i);
            String paramSource = normalizeSourceType(rawParamType);
            String llvmType = typeMapper.toLLVM(paramSource);

            paramSignatures.add(llvmType + " %" + fn.getParams().get(i));
            visitor.putVarType(fn.getParams().get(i),
                    new TypeInfos(paramSource, llvmType, null));
        }

        sb.append("; === Função: ").append(irName).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(irName)
                .append("(").append(String.join(", ", paramSignatures)).append(") {\nentry:\n");

        for (int i = 0; i < fn.getParams().size(); i++) {
            String paramName = fn.getParams().get(i);
            TypeInfos info = visitor.getVarType(paramName);
            String paramType = info.getLLVMType();
            String paramPtr = "%" + paramName + "_addr";

            sb.append("  ").append(paramPtr).append(" = alloca ").append(paramType).append("\n");
            sb.append("  store ").append(paramType).append(" %").append(paramName)
                    .append(", ").append(paramType).append("* ").append(paramPtr).append("\n")
                    .append(";;VAL:").append(paramPtr).append(";;TYPE:").append(paramType).append("\n");

            visitor.getVariableEmitter().registerVarPtr(paramName, paramPtr);
        }

        for (ASTNode stmt : fn.getBody()) {
            sb.append(stmt.accept(visitor));
        }

        if ("void".equals(llvmRetType) && !containsReturn(fn)) {
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }

    private String normalizeSourceType(String t) {
        if (t == null) return "void";
        t = t.trim();
        if (t.startsWith("Struct<") && t.endsWith(">")) {
            return t.substring(7, t.length() - 1).trim();
        }
        return t;
    }

    private boolean containsReturn(FunctionNode fn) {
        return fn.getBody().stream().anyMatch(n -> n instanceof ReturnNode);
    }
}
