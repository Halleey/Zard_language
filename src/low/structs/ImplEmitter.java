package low.structs;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import low.module.LLVisitorMain;
import low.main.TypeInfos;

import java.util.Map;

public class ImplEmitter {
    private final LLVisitorMain visitor;

    public ImplEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public String emit(ImplNode node) {
        StringBuilder llvm = new StringBuilder();
        String baseStruct = node.getStructName();
        boolean hasUsageForBase = visitor.specializedStructs.keySet().stream()
                        .anyMatch(name -> name.equals(baseStruct) || name.startsWith(baseStruct + "<"));

        if (!hasUsageForBase) {
            return "";
        }

        llvm.append(";; ==== Impl Definitions ====\n");


        llvm.append(";; ==== Impl Definitions ====\n");

        boolean temEspecializacao = !visitor.specializedStructs.isEmpty();

        for (FunctionNode fn : node.getMethods()) {
            if (isListImplMethod(baseStruct, fn)) {
                // GERA SOMENTE AS VERSÕES ESPECIALIZADAS
                for (Map.Entry<String, StructNode> entry : visitor.specializedStructs.entrySet()) {
                    String name = entry.getKey();
                    if (name.startsWith(baseStruct + "<")) {
                        String inner = extractInnerType(name);
                        llvm.append("; === Impl especializada para Struct<")
                                .append(baseStruct).append("<").append(inner).append(">> ===\n");
                        llvm.append(generateFunctionImpl(baseStruct, fn, inner));
                    }
                }
            } else {
                llvm.append(emitSimpleMethod(baseStruct, fn));
            }
        }


        llvm.append("\n");
        return llvm.toString();
    }

    private boolean isListImplMethod(String baseStruct, FunctionNode fn) {
        return "Set".equals(baseStruct)
                && fn.getParams() != null
                && fn.getParams().size() >= 2;
    }

    private String emitSimpleMethod(String baseStruct, FunctionNode fn) {
        StringBuilder sb = new StringBuilder();

        String fnName = baseStruct + "_" + fn.getName();
        String structLLVM = "%" + baseStruct;
        String paramTypeLLVM = structLLVM + "*";
        String retType = paramTypeLLVM;

        sb.append("; === Método: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %self) {\n");
        sb.append("entry:\n");

        if (fn.getBody() != null) {
            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(visitor));
            }
        }

        sb.append("  ret ").append(retType).append(" %self\n");
        sb.append("}\n\n");

        return sb.toString();
    }
    private String generateFunctionImpl(String baseStruct, FunctionNode fn, String specialized) {
        StringBuilder sb = new StringBuilder();

        String declaredType = null;
        if (fn.getParamTypes() != null && fn.getParamTypes().size() > 1) {
            declaredType = fn.getParamTypes().get(1);
            if (declaredType != null) declaredType = declaredType.trim();
        }

        String fnName;
        String structLLVM;
        String paramTypeLLVM;

        if (specialized == null) {
            fnName = baseStruct + "_" + fn.getName();
            structLLVM = "%" + baseStruct;
            paramTypeLLVM = structLLVM + "*";
        } else {
            fnName = baseStruct + "_" + specialized + "_" + fn.getName();
            structLLVM = "%" + baseStruct + "_" + specialized;
            paramTypeLLVM = structLLVM + "*";
        }

        String valueTypeLLVM;
        boolean declaredIsGeneric = declaredType != null && declaredType.contains("?");

        if (declaredType != null && !declaredIsGeneric) {
            valueTypeLLVM = mapToLLVMType(declaredType);
        } else if (specialized != null) {
            valueTypeLLVM = mapToLLVMType(specialized);
        } else {

            valueTypeLLVM = "i8*";
        }

        String retType = paramTypeLLVM;
        String paramS = fn.getParams().get(0);
        String valueS = fn.getParams().size() > 1 ? fn.getParams().get(1) : "value";

        sb.append("; === Função: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(valueTypeLLVM).append(" %").append(valueS).append(") {\n");
        sb.append("entry:\n");

        // === Aloca parâmetros no stack ===
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

        String structSourceType;
        String structLLVMType;
        if (specialized != null) {
            structSourceType = "Struct<" + baseStruct + "<" + specialized + ">>";
            structLLVMType = "%" + baseStruct + "_" + specialized + "*";
        } else {
            structSourceType = "Struct<" + baseStruct + ">";
            structLLVMType = "%" + baseStruct + "*";
        }

        visitor.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));

        // ================= COERÊNCIA DE VALOR =================
        String sourceType;
        String llvmType;

        if (declaredType != null && !declaredIsGeneric) {
            sourceType = declaredType;
            llvmType = mapToLLVMType(declaredType);
        }
        else if (specialized != null && (declaredType == null || declaredType.contains("?"))) {
            sourceType = specialized;
            llvmType = mapToLLVMType(specialized);
        }
        else {
            sourceType = "?";
            llvmType = "i8*";
        }

        visitor.putVarType(valueS, new TypeInfos(sourceType, llvmType, null));
        if (fn.getBody() != null && !fn.getBody().isEmpty()) {
            if (specialized != null)
                visitor.enterTypeSpecialization(specialized);

            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(visitor));
            }

            if (specialized != null)
                visitor.exitTypeSpecialization();
        }

        sb.append("  ret ").append(retType).append(" %").append(paramS).append("\n");
        sb.append("}\n\n");
        return sb.toString();
    }

    private String extractInnerType(String s) {
        int start = s.indexOf('<');
        int end = s.indexOf('>');
        if (start == -1 || end == -1) return "?";
        return s.substring(start + 1, end).trim();
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
