package low.structs;

import ast.ASTNode;
import ast.functions.FunctionNode;
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
            // Esperado: "Set<int>", "Pessoa<double>", etc.
            if (key.startsWith(baseStruct + "<") && key.endsWith(">")) {
                System.out.println("[DEBUG hasSpecializations] ENCONTREI specialization: " + key);
                return true;
            }
        }

        System.out.println("[DEBUG hasSpecializations] NENHUMA specialization pra " + baseStruct);
        return false;
    }

    public String emit(ImplNode node) {

        System.out.println("\n=== [DEBUG ImplEmitter.emit] ===");
        System.out.println("Impl de struct: " + node.getStructName());
        System.out.println("Métodos:");
        for (FunctionNode fn : node.getMethods()) {
            System.out.println("  - " + fn.getName() + "  params=" + fn.getParams() +
                    "  types=" + fn.getParamTypes());
        }
        System.out.println("=================================\n");

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

                    System.out.println("[DEBUG ImplEmitter] Gerando impl especializada para "
                            + baseStruct + "<" + inner + "> método " + fn.getName());

                    llvm.append("; === Impl especializada para Struct<")
                            .append(baseStruct).append("<").append(inner).append(">> ===\n");
                    llvm.append(generateFunctionImpl(baseStruct, fn, inner));
                }

            } else {

                System.out.println("[DEBUG ImplEmitter] Gerando impl simples (NÃO especializada) para " + fn.getName());
                llvm.append(emitSimpleMethod(baseStruct, fn));
            }
        }

        llvm.append("\n");
        return llvm.toString();
    }

    private String emitSimpleMethod(String baseStruct, FunctionNode fn) {

        System.out.println("\n=== [DEBUG ImplEmitter.emitSimpleMethod] ===");
        System.out.println("Struct base: " + baseStruct);
        System.out.println("Método: " + fn.getName());
        System.out.println("Params: " + fn.getParams());
        System.out.println("ParamTypes: " + fn.getParamTypes());
        System.out.println("==========================================\n");

        StringBuilder sb = new StringBuilder();

        String fnName = baseStruct + "_" + fn.getName();
        String structLLVM = "%" + baseStruct;
        String paramTypeLLVM = structLLVM + "*";

        String declaredRet = fn.getReturnType();
        if (declaredRet == null) declaredRet = "void";
        declaredRet = declaredRet.trim();

        String retType;
        boolean returnsSelf = false;

        if ("void".equals(declaredRet)) retType = "void";
        else if (declaredRet.startsWith("Struct<" + baseStruct)) {
            retType = paramTypeLLVM;
            returnsSelf = true;
        } else retType = mapToLLVMType(declaredRet);

        String receiverName = (fn.getParams() != null && !fn.getParams().isEmpty())
                ? fn.getParams().get(0)
                : "s";

        sb.append("; === Método: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %").append(receiverName).append(") {\n");
        sb.append("entry:\n");

        // aloca receiver
        String receiverPtr = "%" + receiverName + "_addr";
        sb.append("  ").append(receiverPtr)
                .append(" = alloca ").append(paramTypeLLVM).append("\n");

        sb.append("  store ").append(paramTypeLLVM).append(" %").append(receiverName)
                .append(", ").append(paramTypeLLVM).append("* ").append(receiverPtr).append("\n");

        visitor.getVariableEmitter().registerVarPtr(receiverName, receiverPtr);
        visitor.putVarType(receiverName,
                new TypeInfos("Struct<" + baseStruct + ">", paramTypeLLVM, null));

        List<String> params = fn.getParams();
        List<String> types = fn.getParamTypes();

        if (params != null && types != null) {

            System.out.println("[DEBUG emitSimpleMethod] Registrando parâmetros extras:");

            for (int i = 1; i < params.size(); i++) {
                String pName = params.get(i);
                String srcType = (i < types.size()) ? types.get(i) : "?";

                System.out.println("   param " + pName + " type=" + srcType);

                if (srcType == null || srcType.equals("?")) srcType = "?";

                String llvmType = mapToLLVMType(srcType);
                String ptr = "%" + pName + "_addr";

                sb.append("  ").append(ptr).append(" = alloca ")
                        .append(llvmType).append("\n");

                sb.append("  store ").append(llvmType).append(" %").append(pName)
                        .append(", ").append(llvmType).append("* ").append(ptr).append("\n");

                visitor.getVariableEmitter().registerVarPtr(pName, ptr);
                visitor.putVarType(pName, new TypeInfos(srcType, llvmType, null));
            }
        }

        if (fn.getBody() != null) {
            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(visitor));
            }
        }

        if ("void".equals(retType)) sb.append("  ret void\n");
        else if (returnsSelf) sb.append("  ret ").append(retType).append(" %").append(receiverName).append("\n");
        else sb.append("  ret ").append(retType).append(" undef\n");

        sb.append("}\n\n");
        return sb.toString();
    }

    private String generateFunctionImpl(String baseStruct, FunctionNode fn, String specialized) {

        System.out.println("\n=== [DEBUG generateFunctionImpl] ===");
        System.out.println("baseStruct=" + baseStruct);
        System.out.println("método=" + fn.getName());
        System.out.println("specialized=" + specialized);
        System.out.println("ParamTypes originais=" + fn.getParamTypes());
        System.out.println("=====================================\n");

        StringBuilder sb = new StringBuilder();

        String declaredType = null;
        if (fn.getParamTypes() != null && fn.getParamTypes().size() > 1) {
            declaredType = fn.getParamTypes().get(1);
            if (declaredType != null) declaredType = declaredType.trim();
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

        System.out.println("[DEBUG] Tipo final do parâmetro 'value' = " + valueTypeLLVM);

        String declaredRet = fn.getReturnType();
        if (declaredRet == null) declaredRet = "void";
        declaredRet = declaredRet.trim();

        String retType;
        boolean returnsSelf = false;

        if ("void".equals(declaredRet)) retType = "void";
        else if (declaredRet.startsWith("Struct<" + baseStruct)) {
            retType = paramTypeLLVM;
            returnsSelf = true;
        } else retType = mapToLLVMType(declaredRet);

        String paramS = fn.getParams().get(0);
        String valueS = fn.getParams().size() > 1 ? fn.getParams().get(1) : "value";

        sb.append("; === Função: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(valueTypeLLVM).append(" %").append(valueS)
                .append(") {\n");
        sb.append("entry:\n");

        sb.append("  %").append(paramS).append("_addr = alloca ").append(paramTypeLLVM).append("\n");
        sb.append("  store ").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(paramTypeLLVM).append("* %").append(paramS).append("_addr\n");

        sb.append("  %").append(valueS).append("_addr = alloca ").append(valueTypeLLVM).append("\n");
        sb.append("  store ").append(valueTypeLLVM).append(" %").append(valueS)
                .append(", ").append(valueTypeLLVM).append("* %").append(valueS).append("_addr\n");

        String paramPtr = "%" + paramS + "_addr";
        String valuePtr = "%" + valueS + "_addr";

        visitor.getVariableEmitter().registerVarPtr(paramS, paramPtr);
        visitor.getVariableEmitter().registerVarPtr(valueS, valuePtr);

        String structSourceType = "Struct<" + baseStruct + "<" + specialized + ">>";
        String structLLVMType = "%" + baseStruct + "_" + specialized + "*";

        visitor.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));

        String sourceType;
        String llvmType;

        if (declaredType != null && !declaredIsGeneric) {
            sourceType = declaredType;
            llvmType = mapToLLVMType(declaredType);
        } else {
            sourceType = specialized;
            llvmType = mapToLLVMType(specialized);
        }

        System.out.println("[DEBUG] Registrando valueS='" + valueS + "' como sourceType=" + sourceType + " llvm=" + llvmType);

        visitor.putVarType(valueS, new TypeInfos(sourceType, llvmType, null));

        if (fn.getBody() != null && !fn.getBody().isEmpty()) {

            LLVisitorMain isolated = visitor.fork();

            isolated.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));
            isolated.putVarType(valueS, new TypeInfos(sourceType, llvmType, null));

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

        if ("void".equals(retType)) sb.append("  ret void\n");
        else if (returnsSelf) sb.append("  ret ").append(retType).append(" %").append(paramS).append("\n");
        else sb.append("  ret ").append(retType).append(" undef\n");

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
