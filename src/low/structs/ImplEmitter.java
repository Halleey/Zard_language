package low.structs;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.main.TypeInfos;

import java.util.Map;

public class ImplEmitter {
    private final LLVisitorMain visitor;

    public ImplEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
    }


    private boolean hasSpecializations(String baseStruct) {
        for (String name : visitor.specializedStructs.keySet()) {
            // esperamos algo como "Set<int>" ou "Test<int>"
            if (name.startsWith(baseStruct + "<")) {
                String inner = extractInnerType(name);
                if (inner != null && !inner.isEmpty() && !inner.equals("?")) {
                    return true;
                }
            }
        }
        return false;
    }

    public String emit(ImplNode node) {
        StringBuilder llvm = new StringBuilder();
        String baseStruct = node.getStructName();


        llvm.append(";; ==== Impl Definitions ====\n");

        boolean hasSpecs = hasSpecializations(baseStruct);

        for (FunctionNode fn : node.getMethods()) {

            if (hasSpecs) {
                // Para QUALQUER struct que tenha versões especializadas: Set<int>, Test<int>...
                for (Map.Entry<String, StructNode> entry : visitor.specializedStructs.entrySet()) {
                    String name = entry.getKey();
                    if (name.startsWith(baseStruct + "<")) {
                        String inner = extractInnerType(name);  // ex: "int" em "Test<int>"
                        if (inner == null || inner.isEmpty() || "?".equals(inner)) {
                            continue; // não gera especialização inválida
                        }

                        llvm.append("; === Impl especializada para Struct<")
                                .append(baseStruct).append("<").append(inner).append(">> ===\n");
                        llvm.append(generateFunctionImpl(baseStruct, fn, inner));
                    }
                }

            } else {
                // Só cai aqui pra structs realmente não-genéricas
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

        // --- descobrir tipo de retorno LLVM ---
        String declaredRet = fn.getReturnType();
        if (declaredRet == null) {
            declaredRet = "void";
        }
        declaredRet = declaredRet.trim();

        String retType;
        boolean returnsSelf = false;

        if ("void".equals(declaredRet)) {
            retType = "void";
        } else if (declaredRet.startsWith("Struct<" + baseStruct)) {
            // ex: function Struct<Pessoa> hello()
            retType = paramTypeLLVM; // %Pessoa*
            returnsSelf = true;
        } else {
            // casos futuros (int, double, etc.)
            retType = mapToLLVMType(declaredRet);
        }

        // --- nome do receiver (geralmente "s" ou "self") ---
        String receiverName = (fn.getParams() != null && !fn.getParams().isEmpty())
                ? fn.getParams().get(0)
                : "s";

        sb.append("; === Método: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %").append(receiverName).append(") {\n");
        sb.append("entry:\n");

        // aloca receiver na pilha
        String receiverPtr = "%" + receiverName + "_addr";
        sb.append("  ").append(receiverPtr).append(" = alloca ").append(paramTypeLLVM).append("\n");
        sb.append("  store ").append(paramTypeLLVM).append(" %").append(receiverName)
                .append(", ").append(paramTypeLLVM).append("* ").append(receiverPtr).append("\n");

        // registra tipo e ponteiro do receiver
        visitor.getVariableEmitter().registerVarPtr(receiverName, receiverPtr);
        visitor.putVarType(
                receiverName,
                new TypeInfos("Struct<" + baseStruct + ">", paramTypeLLVM, null)
        );

        // corpo do método
        if (fn.getBody() != null) {
            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(visitor));
            }
        }

        // retorno
        if ("void".equals(retType)) {
            sb.append("  ret void\n");
        } else if (returnsSelf) {
            sb.append("  ret ").append(retType).append(" %").append(receiverName).append("\n");
        } else {
            // fallback simples (se um dia você suportar retorno não-self aqui)
            sb.append("  ret ").append(retType).append(" undef\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }

    /**
     * Caminho especializado para structs com tipo interno: Set<int>, Test<int>, etc.
     */
    private String generateFunctionImpl(String baseStruct, FunctionNode fn, String specialized) {
        StringBuilder sb = new StringBuilder();

        // Tipo declarado do segundo arg (ex: ?, int, boolean, string)
        String declaredType = null;
        if (fn.getParamTypes() != null && fn.getParamTypes().size() > 1) {
            declaredType = fn.getParamTypes().get(1);
            if (declaredType != null) declaredType = declaredType.trim();
        }

        String fnName;
        String structLLVM;
        String paramTypeLLVM;

        // Aqui SEMPRE queremos a forma especializada: %Set_int, %Test_int etc.
        fnName = baseStruct + "_" + specialized + "_" + fn.getName();
        structLLVM = "%" + baseStruct + "_" + specialized;
        paramTypeLLVM = structLLVM + "*";

        // Tipo LLVM do "value" (segundo param)
        String valueTypeLLVM;
        boolean declaredIsGeneric = declaredType != null && declaredType.contains("?");

        if (declaredType != null && !declaredIsGeneric) {
            valueTypeLLVM = mapToLLVMType(declaredType);
        } else if (specialized != null) {
            valueTypeLLVM = mapToLLVMType(specialized);
        } else {
            // fallback genérico
            valueTypeLLVM = "i8*";
        }

        String declaredRet = fn.getReturnType();
        if (declaredRet == null) {
            declaredRet = "void";
        }
        declaredRet = declaredRet.trim();

        String retType;
        boolean returnsSelf = false;

        if ("void".equals(declaredRet)) {
            retType = "void";
        } else if (declaredRet.startsWith("Struct<" + baseStruct)) {
            // ex: function Struct<Set> add(...)
            retType = paramTypeLLVM; // %Set_int*
            returnsSelf = true;
        } else {
            retType = mapToLLVMType(declaredRet);
        }

        String paramS = fn.getParams().get(0);
        String valueS = fn.getParams().size() > 1 ? fn.getParams().get(1) : "value";

        sb.append("; === Função: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(valueTypeLLVM).append(" %").append(valueS).append(") {\n");
        sb.append("entry:\n");

        // === Aloca parametros no stack ===
        sb.append("  %").append(paramS).append("_addr = alloca ").append(paramTypeLLVM).append("\n");
        sb.append("  store ").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(paramTypeLLVM).append("* %").append(paramS).append("_addr\n");

        sb.append("  %").append(valueS).append("_addr = alloca ").append(valueTypeLLVM).append("\n");
        sb.append("  store ").append(valueTypeLLVM).append(" %").append(valueS)
                .append(", ").append(valueTypeLLVM).append("* %").append(valueS).append("_addr\n");

        String paramPtr = "%" + paramS + "_addr";
        String valuePtr = "%" + valueS + "_addr";

        // registra ponteiros no emissor de variáveis do visitor "pai"
        visitor.getVariableEmitter().registerVarPtr(paramS, paramPtr);
        visitor.getVariableEmitter().registerVarPtr(valueS, valuePtr);

        // Tipos fonte / LLVM que vamos usar no ambiente do visitor
        String structSourceType = "Struct<" + baseStruct + "<" + specialized + ">>";
        String structLLVMType = "%" + baseStruct + "_" + specialized + "*";

        visitor.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));

        String sourceType;
        String llvmType;

        if (declaredType != null && !declaredIsGeneric) {
            sourceType = declaredType;
            llvmType = mapToLLVMType(declaredType);
        } else if (specialized != null && (declaredType == null || declaredType.contains("?"))) {
            sourceType = specialized;
            llvmType = mapToLLVMType(specialized);
        } else {
            sourceType = "?";
            llvmType = "i8*";
        }
        visitor.putVarType(valueS, new TypeInfos(sourceType, llvmType, null));

        // corpo da função
        if (fn.getBody() != null && !fn.getBody().isEmpty()) {

            // Fork do visitor para não sujar contexto global
            LLVisitorMain isolated = visitor.fork();

            // Replicamos o ambiente de tipos dentro do visitor isolado
            isolated.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));
            isolated.putVarType(valueS, new TypeInfos(sourceType, llvmType, null));

            isolated.getVariableEmitter().registerVarPtr(paramS, paramPtr);
            isolated.getVariableEmitter().registerVarPtr(valueS, valuePtr);

            // Informa ao visitor que estamos em uma especialização (int, boolean, string, etc.)
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

        // retorno
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


    private String extractInnerType(String s) {
        int start = s.indexOf('<');
        int end = s.indexOf('>');
        if (start == -1 || end == -1 || end <= start + 1) return "?";
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
