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
        System.out.println("##########################################");
        System.out.println("[FEmit] emit() INÍCIO");
        System.out.println("[FEmit] fn.getName() = " + fn.getName());
        System.out.println("[FEmit] implStructName = " + fn.getImplStructName());

        StringBuilder sb = new StringBuilder();

        String baseName = fn.getName();
        String implOwner = fn.getImplStructName(); // ex: Set_int
        String irName = (implOwner != null && !implOwner.isEmpty())
                ? implOwner + "_" + baseName
                : baseName;

        System.out.println("[FEmit] baseName = " + baseName);
        System.out.println("[FEmit] implOwner = " + implOwner);
        System.out.println("[FEmit] irName (nome LLVM) = " + irName);
        visitor.functions.put(irName, fn);
        // registro temporário para permitir dedução recursiva
        System.out.println("[FEmit] Registrando tipo PROVISÓRIO any/void para " + irName);
        visitor.registerFunctionType(irName, new TypeInfos("any", "void", null));

        String declaredType = normalizeSourceType(fn.getReturnType());
        System.out.println("[FEmit] declaredType (fonte) = " + declaredType);
        TypeInfos retInfo;

        if ("void".equals(declaredType) && containsReturn(fn)) {
            System.out.println("[FEmit] declaredType = void mas função contém return, iniciando dedução");
            visitor.getCallEmitter().markBeingDeduced(baseName);
            retInfo = returnInferer.deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(baseName);
            System.out.println("[FEmit] deduceReturnType result: srcType=" + retInfo.getSourceType()
                    + " llvmType=" + retInfo.getLLVMType());
        } else {
            String llvm = typeMapper.toLLVM(declaredType);
            retInfo = new TypeInfos(declaredType, llvm, null);
            System.out.println("[FEmit] retorno declarado diretamente: srcType=" + declaredType
                    + " llvmType=" + llvm);
        }

        System.out.println("[FEmit] Registrando tipo DEFINITIVO para função " + irName);
        visitor.registerFunctionType(irName, retInfo);
        String llvmRetType = retInfo.getLLVMType();
        System.out.println("[FEmit] llvmRetType FINAL = " + llvmRetType);

        List<String> paramSignatures = new ArrayList<>();

        System.out.println("[FEmit] Registrando tipos lógicos dos parâmetros:");
        for (ParamInfo p : fn.getParameters()) {
            String srcType = normalizeSourceType(p.type());    // ex: "int"
            String valueLLVM = typeMapper.toLLVM(srcType);     // ex: "i32"

            System.out.println("  [FEmit] param name=" + p.name() +
                    " srcType=" + srcType +
                    " valueLLVM=" + valueLLVM +
                    " isRef=" + p.isRef());

            visitor.putVarType(
                    p.name(),
                    new TypeInfos(srcType, valueLLVM, null)
            );
        }

        System.out.println("[FEmit] Montando assinatura LLVM dos parâmetros:");
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

            System.out.println("  [FEmit] assinatura param " + p.name()
                    + ": " + llvmInSignature + " (isRef=" + p.isRef() + ")");

            paramSignatures.add(llvmInSignature + " %" + p.name());
        }

        sb.append("; === Função: ").append(irName).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(irName)
                .append("(").append(String.join(", ", paramSignatures)).append(") {\nentry:\n");

        // ========= ALOCAÇÃO / REGISTRO DE PONTEIROS =========
        System.out.println("[FEmit] Gerando alloca/store para parâmetros (quando necessário)");
        for (ParamInfo p : fn.getParameters()) {

            String paramName = p.name();
            TypeInfos info = visitor.getVarType(paramName);
            String valueLLVM = info.getLLVMType();        // tipo de valor (ex: i32)
            String paramPtrName = "%" + paramName + "_addr";

            if (p.isRef()) {
                System.out.println("[FEmit] &param " + paramName +
                        " → já recebemos T*, registrando ponteiro real como '%" + paramName + "'");
                // &param → já recebemos um ponteiro T*; não fazemos alloca.
                visitor.getVariableEmitter().registerVarPtr(paramName, "%" + paramName);
            } else {
                System.out.println("[FEmit] param normal " + paramName +
                        " → criando alloca " + paramPtrName + " do tipo " + valueLLVM);
                sb.append("  ").append(paramPtrName)
                        .append(" = alloca ").append(valueLLVM).append("\n");
                sb.append("  store ").append(valueLLVM)
                        .append(" %").append(paramName)
                        .append(", ").append(valueLLVM).append("* ")
                        .append(paramPtrName).append("\n")
                        .append(";;VAL:").append(paramPtrName)
                        .append(";;TYPE:").append(valueLLVM).append("\n");

                visitor.getVariableEmitter().registerVarPtr(paramName, paramPtrName);
            }
        }

        // ========= CORPO =========
        System.out.println("[FEmit] Emitindo corpo da função " + irName
                + " com " + fn.getBody().size() + " statements");
        for (ASTNode stmt : fn.getBody()) {
            sb.append(stmt.accept(visitor));
        }

        if ("void".equals(llvmRetType) && !containsReturn(fn)) {
            System.out.println("[FEmit] Função void sem return explícito, emitindo 'ret void'");
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");
        System.out.println("[FEmit] emit() FIM para " + irName);
        System.out.println("##########################################");
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
