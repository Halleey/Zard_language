package low.structs;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import low.module.LLVisitorMain;

import java.util.Map;

import low.main.TypeInfos;

public class ImplEmitter {
    private final LLVisitorMain visitor;

    public ImplEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public String emit(ImplNode node) {
        StringBuilder llvm = new StringBuilder();
        String baseStruct = node.getStructName();

        llvm.append(";; ==== Impl Definitions ====\n");

        boolean temEspecializacao = !visitor.specializedStructs.isEmpty();

        for (FunctionNode fn : node.getMethods()) {
            if (isListImplMethod(baseStruct, fn)) {
                // só gera genérica se não há especialização inferida
                if (!temEspecializacao) {
                    llvm.append("; === Impl genérica para Struct<")
                            .append(baseStruct).append("> ===\n");
                    llvm.append(generateFunctionImpl(baseStruct, fn, null));
                }

                // gera versões especializadas (ex: Struct<Set<int>>)
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

        String fnName;
        String structLLVM;
        String paramTypeLLVM;
        String valueTypeLLVM;

        if (specialized == null) {
            fnName = baseStruct + "_" + fn.getName();
            structLLVM = "%" + baseStruct;
            paramTypeLLVM = structLLVM + "*";
            valueTypeLLVM = "i8*";
        } else {
            fnName = baseStruct + "_" + specialized + "_" + fn.getName();
            structLLVM = "%" + baseStruct + "_" + specialized;
            paramTypeLLVM = structLLVM + "*";
            valueTypeLLVM = mapToLLVMType(specialized);
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

        // ✅ REGISTRA PONTEIROS DOS PARÂMETROS
        String paramPtr = "%" + paramS + "_addr";
        String valuePtr = "%" + valueS + "_addr";
        visitor.getVariableEmitter().registerVarPtr(paramS, paramPtr);
        visitor.getVariableEmitter().registerVarPtr(valueS, valuePtr);

        // ✅ REGISTRA TIPOS DOS PARÂMETROS
        if (specialized != null) {
            String structSourceType = "Struct<" + baseStruct + "<" + specialized + ">>";
            String structLLVMType = "%" + baseStruct + "_" + specialized + "*";
            visitor.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));
            visitor.putVarType(valueS, new TypeInfos(specialized, mapToLLVMType(specialized), null));
        } else {
            visitor.putVarType(paramS, new TypeInfos("Struct<" + baseStruct + ">", "%" + baseStruct + "*", null));
            visitor.putVarType(valueS, new TypeInfos("?", "i8*", null));
        }

        // === Corpo da função ===
        if (fn.getBody() != null && !fn.getBody().isEmpty()) {
            if (specialized != null)
                visitor.enterTypeSpecialization(specialized);

            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(visitor));
            }

            if (specialized != null)
                visitor.exitTypeSpecialization();
        }

        // === Retorno ===
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
