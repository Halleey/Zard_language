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
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.mappers.LLVMTypeMapper;
import low.module.builders.primitives.LLVMVoid;


public class FunctionEmitter {

    private final LLVisitorMain visitor;
    private final ReturnTypeInferer returnInferer;

    public FunctionEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
        this.returnInferer = new ReturnTypeInferer(visitor);
    }

    public LLVMValue emit(FunctionNode fn) {

        StringBuilder sb = new StringBuilder();

        String baseName = fn.getName();
        Type implStructType = fn.getImplStructType();
        String implOwner = implStructType != null ? implStructType.name() : null;

        String irName = (implOwner != null && !implOwner.isEmpty())
                ? implOwner + "_" + baseName
                : baseName;

        visitor.functions.put(irName, fn);

        // inicialmente registra ANY / void
        visitor.registerFunctionType(irName, new TypeInfos(PrimitiveTypes.ANY, new LLVMVoid()));

        Type declaredType = fn.getReturnType();
        TypeInfos retInfo;

        // se é void mas contém return, deduz o tipo
        if (declaredType instanceof PrimitiveTypes p &&
                "void".equals(p.name()) &&
                containsReturn(fn)) {

            visitor.getCallEmitter().markBeingDeduced(baseName);
            retInfo = returnInferer.deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(baseName);

        } else {
            LLVMTYPES llvmType = LLVMTypeMapper.from(declaredType);
            retInfo = new TypeInfos(declaredType, llvmType);
        }

        visitor.registerFunctionType(irName, retInfo);

        LLVMTYPES llvmRetType = retInfo.getLLVMType();

        List<String> paramSignatures = new ArrayList<>();

        for (ParamInfo p : fn.getParameters()) {
            Type paramType = p.typeObj();
            LLVMTYPES llvmType = LLVMTypeMapper.from(paramType);

            visitor.putVarType(p.name(), new TypeInfos(paramType, llvmType));

            String llvmStr = p.isRef() ? llvmType + "*" : llvmType.toString();
            paramSignatures.add(llvmStr + " %" + p.name());
        }

        sb.append("; === Função: ").append(irName).append(" ===\n");
        sb.append("define ").append(llvmRetType)
                .append(" @").append(irName)
                .append("(").append(String.join(", ", paramSignatures)).append(") {\nentry:\n");

        // parâmetros: criar alocação se não for referência
        for (ParamInfo p : fn.getParameters()) {
            String paramName = p.name();
            TypeInfos info = visitor.getVarType(paramName);
            LLVMTYPES valueType = info.getLLVMType();

            if (p.isRef()) {
                visitor.getVariableEmitter().registerVarPtr(paramName, "%" + paramName);
            } else {
                String ptr = "%" + paramName + "_addr";

                sb.append("  ").append(ptr)
                        .append(" = alloca ").append(valueType).append("\n");

                sb.append("  store ").append(valueType)
                        .append(" %").append(paramName)
                        .append(", ").append(valueType)
                        .append("* ").append(ptr).append("\n");

                visitor.getVariableEmitter().registerVarPtr(paramName, ptr);
            }
        }

        // corpo da função
        for (ASTNode stmt : fn.getBody()) {
            LLVMValue val = stmt.accept(visitor);
            if (val != null && val.getCode() != null && !val.getCode().isBlank()) {
                sb.append(val.getCode());
            }
        }

        if (llvmRetType instanceof LLVMVoid && !containsReturn(fn)) {
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");

        return new LLVMValue(llvmRetType, "%" + irName, sb.toString());
    }

    private boolean containsReturn(FunctionNode fn) {
        for (ASTNode node : fn.getBody()) {
            if (node instanceof ReturnNode) return true;
        }
        return false;
    }
}