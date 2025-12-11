package low.structs;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.main.TypeInfos;

import java.util.ArrayList;
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
        if (params.isEmpty()) {
            // impl sem receiver não faz sentido, mas evita crash
            return "; [ImplEmitter] Ignorando função sem parâmetros em impl de " + baseStruct + "\n\n";
        }

        // ====== Receiver (primeiro parâmetro) ======
        ParamInfo receiverParam = params.get(0);
        String paramS = receiverParam.name(); // normalmente "s"

        String fnName = baseStruct + "_" + specialized + "_" + fn.getName();
        String structLLVM = "%" + baseStruct + "_" + specialized;
        String paramTypeLLVM = structLLVM + "*";

        // ====== Inferência do tipo do "value" (segundo parâmetro genérico) ======
        String declaredType = null;
        if (params.size() > 1) {
            declaredType = params.get(1).type();
            if (declaredType != null) declaredType = declaredType.trim();
        }
        boolean declaredIsGeneric = declaredType != null && declaredType.contains("?");

        String valueTypeLLVM;
        if (declaredType != null && !declaredIsGeneric) {
            valueTypeLLVM = mapToLLVMType(declaredType);
        } else {
            // se for genérico (List<?> / T), usa o tipo especializado
            valueTypeLLVM = mapToLLVMType(specialized);
        }

        // ====== Retorno ======
        String declaredRet = fn.getReturnType();
        if (declaredRet == null) declaredRet = "void";
        declaredRet = declaredRet.trim();

        String retType;
        boolean returnsSelf = false;

        if ("void".equals(declaredRet)) {
            retType = "void";
        } else if (declaredRet.startsWith("Struct<" + baseStruct)) {
            // ex: Struct<Set<int>> -> %Set_int*
            retType = paramTypeLLVM;
            returnsSelf = true;
        } else {
            retType = mapToLLVMType(declaredRet);
        }

        // ====== Montar lista de parâmetros da função ======
        StringBuilder paramList = new StringBuilder();
        paramList.append(paramTypeLLVM).append(" %").append(paramS);

        // Guarda o tipo LLVM de cada parâmetro por índice (0 = receiver)
        List<String> llvmParamTypes = new ArrayList<>();
        llvmParamTypes.add(paramTypeLLVM);

        for (int i = 1; i < params.size(); i++) {
            ParamInfo p = params.get(i);
            String pName = p.name();
            String srcType = p.type();
            if (srcType != null) srcType = srcType.trim();

            String llvmType;

            if (i == 1) {
                // segundo parâmetro respeita a lógica genérica (valueTypeLLVM)
                llvmType = valueTypeLLVM;
            } else {
                // demais usam o tipo declarado normalmente
                llvmType = mapToLLVMType(srcType);
            }

            llvmParamTypes.add(llvmType);
            paramList.append(", ").append(llvmType).append(" %").append(pName);
        }

        // ====== Cabeçalho da função ======
        sb.append("; === Função: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramList).append(") {\n");
        sb.append("entry:\n");

        // ====== Aloca receiver ======
        String receiverPtr = "%" + paramS + "_addr";
        sb.append("  ").append(receiverPtr)
                .append(" = alloca ").append(paramTypeLLVM).append("\n");
        sb.append("  store ").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(paramTypeLLVM).append("* ").append(receiverPtr).append("\n");

        String structSourceType = "Struct<" + baseStruct + "<" + specialized + ">>";
        String structLLVMType = paramTypeLLVM;

        // Registra receiver no visitor principal (mas vamos usar um fork para o corpo)
        visitor.getVariableEmitter().registerVarPtr(paramS, receiverPtr);
        visitor.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));

        // ====== Aloca e registra todos os outros parâmetros ======
        // i = índice do parâmetro na lista original
        for (int i = 1; i < params.size(); i++) {

            ParamInfo p = params.get(i);
            String pName = p.name();
            String srcType = p.type();
            if (srcType != null) srcType = srcType.trim();

            String llvmType;
            String sourceType;

            if (i == 1) {
                // parâmetro "genérico" (id / v / etc.)
                if (declaredType != null && !declaredIsGeneric) {
                    sourceType = declaredType;
                    llvmType = valueTypeLLVM;
                } else {
                    sourceType = specialized;
                    llvmType = valueTypeLLVM;
                }
            } else {
                sourceType = srcType;
                llvmType = llvmParamTypes.get(i);
            }

            String ptr = "%" + pName + "_addr";

            sb.append("  ").append(ptr)
                    .append(" = alloca ").append(llvmType).append("\n");
            sb.append("  store ").append(llvmType).append(" %").append(pName)
                    .append(", ").append(llvmType).append("* ").append(ptr).append("\n");

            visitor.getVariableEmitter().registerVarPtr(pName, ptr);
            visitor.putVarType(pName, new TypeInfos(sourceType, llvmType, null));
        }

        // ====== Corpo usando um visitor isolado ======
        if (fn.getBody() != null && !fn.getBody().isEmpty()) {

            LLVisitorMain isolated = visitor.fork();

            // Copia tipos e ponteiros dos parâmetros para o fork
            isolated.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));
            isolated.getVariableEmitter().registerVarPtr(paramS, receiverPtr);

            for (int i = 1; i < params.size(); i++) {
                ParamInfo p = params.get(i);
                String pName = p.name();
                String llvmType = llvmParamTypes.get(i);

                String srcType;
                if (i == 1) {
                    // genérico
                    if (declaredType != null && !declaredIsGeneric) {
                        srcType = declaredType;
                    } else {
                        srcType = specialized;
                    }
                } else {
                    srcType = p.type();
                }
                String ptr = "%" + pName + "_addr";

                isolated.putVarType(pName, new TypeInfos(srcType, llvmType, null));
                isolated.getVariableEmitter().registerVarPtr(pName, ptr);
            }

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

        // ====== Retorno default ======
        if ("void".equals(retType)) {
            sb.append("  ret void\n");
        } else if (returnsSelf) {
            sb.append("  ret ").append(retType).append(" %").append(paramS).append("\n");
        } else {
            sb.append("  ret ").append(retType).append(" undef\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }


    private String mapToLLVMType(String inner) {
        if (inner == null) return "i8*";
        inner = inner.trim();

        // Struct<Nome> → %Nome*
        if (inner.startsWith("Struct<") && inner.endsWith(">")) {
            String name = inner.substring("Struct<".length(), inner.length() - 1).trim();
            return "%" + name + "*";
        }

        return switch (inner) {
            case "int" -> "i32";
            case "double" -> "double";
            case "bool", "boolean" -> "i1";
            case "string", "String" -> "%String*";
            default -> "i8*";
        };
    }

}
