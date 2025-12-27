package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
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
        String implOwner = fn.getImplStructName(); // ex: Set_int
        String irName = (implOwner != null && !implOwner.isEmpty())
                ? implOwner + "_" + baseName
                : baseName;

        visitor.functions.put(irName, fn);
        // registro temporário para permitir dedução recursiva
        visitor.registerFunctionType(irName, new TypeInfos("any", "void", null));

        String declaredType = normalizeSourceType(fn.getReturnType());
        TypeInfos retInfo;

        if ("void".equals(declaredType) && containsReturn(fn)) {
            visitor.getCallEmitter().markBeingDeduced(baseName);
            retInfo = returnInferer.deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(baseName);
        } else {
            String llvm = typeMapper.toLLVM(declaredType);
            retInfo = new TypeInfos(declaredType, llvm, null);
        }

        visitor.registerFunctionType(irName, retInfo);
        String llvmRetType = retInfo.getLLVMType();

        List<String> paramSignatures = new ArrayList<>();
        for (ParamInfo p : fn.getParameters()) {
            String srcType = normalizeSourceType(p.type());    // ex: "int"
            String valueLLVM = typeMapper.toLLVM(srcType);     // ex: "i32"
            visitor.putVarType(
                    p.name(),
                    new TypeInfos(srcType, valueLLVM, null)
            );
        }
        for (ParamInfo p : fn.getParameters()) {
            TypeInfos info = visitor.getVarType(p.name());
            String valueLLVM = info.getLLVMType();

            String llvmInSignature;
            if (p.isRef()) {
                // &T → T* na assinatura
                llvmInSignature = valueLLVM + "*";
            } else {
                llvmInSignature = valueLLVM;
            }


            paramSignatures.add(llvmInSignature + " %" + p.name());
        }

        sb.append("; === Função: ").append(irName).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(irName)
                .append("(").append(String.join(", ", paramSignatures)).append(") {\nentry:\n");

        for (ParamInfo p : fn.getParameters()) {

            String paramName = p.name();
            TypeInfos info = visitor.getVarType(paramName);
            String valueLLVM = info.getLLVMType();        // tipo de valor (ex: i32)
            String paramPtrName = "%" + paramName + "_addr";

            if (p.isRef()) {
                visitor.getVariableEmitter().registerVarPtr(paramName, "%" + paramName);
            } else {
                sb.append("  ").append(paramPtrName)
                        .append(" = alloca ").append(valueLLVM).append("\n");
                sb.append("  store ").append(valueLLVM)
                        .append(" %").append(paramName)
                        .append(", ").append(valueLLVM).append("* ")
                        .append(paramPtrName).append("\n");

                visitor.getVariableEmitter().registerVarPtr(paramName, paramPtrName);

            }

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
        boolean has = fn.getBody().stream().anyMatch(n -> n instanceof ReturnNode);
        System.out.println("[FEmit] containsReturn(" + fn.getName() + ") = " + has);
        return has;
    }
}
