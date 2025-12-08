package low.structs;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.main.TypeInfos;

import java.util.List;
import java.util.Map;

public class ImplEmitter {
    private final LLVisitorMain visitor;

    public ImplEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
    }

    private boolean hasSpecializations(String baseStruct) {
        System.out.println("[DEBUG hasSpecializations] baseStruct=" + baseStruct);
        System.out.println("[DEBUG hasSpecializations] specializedStructs keys="
                + visitor.specializedStructs.keySet());

        for (String key : visitor.specializedStructs.keySet()) {
            // Esperado: "Set<int>", "Pessoa<double>"...
            if (key.startsWith(baseStruct + "<") && key.endsWith(">")) {
                System.out.println("[DEBUG hasSpecializations] ENCONTREI specialization: " + key);
                return true;
            }
        }

        System.out.println("[DEBUG hasSpecializations] NENHUMA specialization pra " + baseStruct);
        return false;
    }

    public String emit(ImplNode node) {

        StringBuilder llvm = new StringBuilder();
        String baseStruct = node.getStructName();

        llvm.append(";; ==== Impl Definitions ====\n");

        boolean hasSpecs = hasSpecializations(baseStruct);

        for (FunctionNode fn : node.getMethods()) {

            if (hasSpecs) {

                for (StructNode spec : visitor.specializedStructs.values()) {

                    String specName = spec.getName();
                    if (!specName.startsWith(baseStruct + "_")) continue;

                    String inner = specName.substring(baseStruct.length() + 1).trim();
                    if (inner.isEmpty() || "?".equals(inner)) continue;

                    llvm.append("; === Impl especializada para Struct<")
                            .append(baseStruct).append("<").append(inner).append(">> ===\n");

                    llvm.append(generateFunctionImpl(baseStruct, fn, inner));
                }

            } else {
                llvm.append(emitSimpleMethod(baseStruct, fn));
            }
        }

        llvm.append("\n");
        return llvm.toString();
    }

    private String emitSimpleMethod(String baseStruct, FunctionNode fn) {

        StringBuilder sb = new StringBuilder();

        String fnName = baseStruct + "_" + fn.getName();
        String structLLVM = "%" + baseStruct;
        String paramTypeLLVM = structLLVM + "*";

        String declaredRet = fn.getReturnType();
        if (declaredRet == null) declaredRet = "void";
        declaredRet = declaredRet.trim();

        String retType;
        boolean returnsSelf = false;

        if ("void".equals(declaredRet)) {
            retType = "void";
        } else if (declaredRet.startsWith("Struct<" + baseStruct)) {
            retType = paramTypeLLVM;
            returnsSelf = true;
        } else {
            retType = mapToLLVMType(declaredRet);
        }

        List<ParamInfo> params = fn.getParameters();

        // === receiver ===
        String receiverName = !params.isEmpty()
                ? params.get(0).name()
                : "s";

        sb.append("; === Método: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %").append(receiverName).append(") {\n");
        sb.append("entry:\n");

        String receiverPtr = "%" + receiverName + "_addr";
        sb.append("  ").append(receiverPtr)
                .append(" = alloca ").append(paramTypeLLVM).append("\n");
        sb.append("  store ").append(paramTypeLLVM).append(" %").append(receiverName)
                .append(", ").append(paramTypeLLVM).append("* ").append(receiverPtr).append("\n");

        visitor.getVariableEmitter().registerVarPtr(receiverName, receiverPtr);
        visitor.putVarType(
                receiverName,
                new TypeInfos("Struct<" + baseStruct + ">", paramTypeLLVM, null)
        );

        // === parâmetros extras ===
        for (int i = 1; i < params.size(); i++) {

            ParamInfo p = params.get(i);
            String pName = p.name();
            String srcType = p.type();

            if (srcType == null || srcType.equals("?")) {
                srcType = "?";
            }

            String llvmType = mapToLLVMType(srcType);
            String ptr = "%" + pName + "_addr";

            sb.append("  ").append(ptr)
                    .append(" = alloca ").append(llvmType).append("\n");

            sb.append("  store ").append(llvmType).append(" %").append(pName)
                    .append(", ").append(llvmType).append("* ").append(ptr).append("\n");

            visitor.getVariableEmitter().registerVarPtr(pName, ptr);
            visitor.putVarType(pName, new TypeInfos(srcType, llvmType, null));
        }

        if (fn.getBody() != null) {
            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(visitor));
            }
        }

        if ("void".equals(retType)) {
            sb.append("  ret void\n");
        } else if (returnsSelf) {
            sb.append("  ret ").append(retType).append(" %").append(receiverName).append("\n");
        } else {
            sb.append("  ret ").append(retType).append(" undef\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }
    private String generateFunctionImpl(String baseStruct, FunctionNode fn, String specialized) {

        StringBuilder sb = new StringBuilder();

        List<ParamInfo> params = fn.getParameters();

        // ===== tipo declarado do "value" (segundo parâmetro) =====
        String declaredType = null;
        if (params.size() > 1) {
            declaredType = params.get(1).type();
            if (declaredType != null) {
                declaredType = declaredType.trim();
            }
        }

        String fnName = baseStruct + "_" + specialized + "_" + fn.getName();
        String structLLVM = "%" + baseStruct + "_" + specialized;
        String paramTypeLLVM = structLLVM + "*";

        boolean declaredIsGeneric = declaredType != null && declaredType.contains("?");

        String valueTypeLLVM;
        if (declaredType != null && !declaredIsGeneric) {
            valueTypeLLVM = mapToLLVMType(declaredType);
        } else {
            valueTypeLLVM = mapToLLVMType(specialized);
        }

        // ===== retorno =====
        String declaredRet = fn.getReturnType();
        if (declaredRet == null) declaredRet = "void";
        declaredRet = declaredRet.trim();

        String retType;
        boolean returnsSelf = false;

        if ("void".equals(declaredRet)) {
            retType = "void";
        } else if (declaredRet.startsWith("Struct<" + baseStruct)) {
            retType = paramTypeLLVM;
            returnsSelf = true;
        } else {
            retType = mapToLLVMType(declaredRet);
        }

        // ===== nomes dos parâmetros =====
        String paramS = params.get(0).name();
        String valueS = params.size() > 1 ? params.get(1).name() : "value";

        sb.append("; === Função: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(valueTypeLLVM).append(" %").append(valueS)
                .append(") {\n");
        sb.append("entry:\n");

        // ===== receiver =====
        sb.append("  %").append(paramS).append("_addr = alloca ").append(paramTypeLLVM).append("\n");
        sb.append("  store ").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(paramTypeLLVM).append("* %")
                .append(paramS).append("_addr\n");

        // ===== value =====
        sb.append("  %").append(valueS).append("_addr = alloca ").append(valueTypeLLVM).append("\n");
        sb.append("  store ").append(valueTypeLLVM).append(" %").append(valueS)
                .append(", ").append(valueTypeLLVM).append("* %")
                .append(valueS).append("_addr\n");

        String paramPtr = "%" + paramS + "_addr";
        String valuePtr = "%" + valueS + "_addr";

        visitor.getVariableEmitter().registerVarPtr(paramS, paramPtr);
        visitor.getVariableEmitter().registerVarPtr(valueS, valuePtr);

        String structSourceType = "Struct<" + baseStruct + "<" + specialized + ">>";
        String structLLVMType = "%" + baseStruct + "_" + specialized + "*";

        visitor.putVarType(paramS,
                new TypeInfos(structSourceType, structLLVMType, null));

        String sourceType;
        String llvmType;

        if (declaredType != null && !declaredIsGeneric) {
            sourceType = declaredType;
            llvmType = mapToLLVMType(declaredType);
        } else {
            sourceType = specialized;
            llvmType = mapToLLVMType(specialized);
        }

        visitor.putVarType(valueS, new TypeInfos(sourceType, llvmType, null));

        // ===== corpo =====
        if (fn.getBody() != null && !fn.getBody().isEmpty()) {

            LLVisitorMain isolated = visitor.fork();

            isolated.putVarType(paramS,
                    new TypeInfos(structSourceType, structLLVMType, null));
            isolated.putVarType(valueS,
                    new TypeInfos(sourceType, llvmType, null));

            isolated.getVariableEmitter().registerVarPtr(paramS, paramPtr);
            isolated.getVariableEmitter().registerVarPtr(valueS, valuePtr);

            if (specialized != null) {
                isolated.enterTypeSpecialization(specialized);
            }

            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(isolated));
            }

            if (specialized != null) {
                isolated.exitTypeSpecialization();
            }
        }

        if ("void".equals(retType)) {
            sb.append("  ret void\n");
        } else if (returnsSelf) {
            sb.append("  ret ").append(retType)
                    .append(" %").append(paramS).append("\n");
        } else {
            sb.append("  ret ").append(retType).append(" undef\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }


    private String mapToLLVMType(String inner) {
        return switch (inner) {
            case "int" -> "i32";
            case "double" -> "double";
            case "bool", "boolean" -> "i1";
            case "string", "String" -> "%String*";
            default -> "i8*";
        };
    }
}
