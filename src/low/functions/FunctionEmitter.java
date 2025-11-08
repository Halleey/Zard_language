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

    private static boolean isPrimitive(String t) {
        return "int".equals(t) || "double".equals(t)
                || "bool".equals(t) || "string".equals(t)
                || "void".equals(t);
    }

    private static String normalizeSourceType(String t) {
        if (t == null) return "void";
        t = t.trim();
        if (t.startsWith("Struct<") && t.endsWith(">")) {
            String inner = t.substring(7, t.length() - 1).trim();
            if (isPrimitive(inner)) return inner; // unwrap Struct<int> → int
            return t;
        }
        if ("%int".equals(t)) return "int";
        if ("%double".equals(t)) return "double";
        if ("%bool".equals(t)) return "bool";
        if ("%String*".equals(t)) return "string";
        return t;
    }

    public String emit(FunctionNode fn) {
        StringBuilder sb = new StringBuilder();

        String baseName = fn.getName();
        String implOwner = fn.getImplStructName();
        String irName;

        if (implOwner != null && !implOwner.isEmpty()) {
            // método de impl: Set + add -> Set_add
            irName = implOwner + "_" + baseName;
        } else {
            // função normal
            irName = baseName;
        }

        // registra tipo "any" primeiro pra evitar dependências cíclicas
        visitor.registerFunctionType(irName, new TypeInfos("any", "void", null));

        String declaredType = normalizeSourceType(fn.getReturnType());

        TypeInfos retInfo;
        if ("void".equals(declaredType) && containsReturn(fn)) {
            // aqui eu mantive o mark/unmark com o nome lógico da função
            // (não precisa ser o mangleado)
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

        // registra o tipo final usando SEMPRE o nome do IR (mangleado ou não)
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

        // aloca e registra parâmetros
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
            if (stmt instanceof ReturnNode ret) {
                if (ret.expr instanceof LiteralNode lit && lit.value.type().equals("string")) {
                    String literal = (String) lit.value.value();
                    String strName = visitor.getGlobalStrings().getOrCreateString(literal);
                    int len = visitor.getGlobalStrings().getLength(literal);

                    String tmpStruct = visitor.getTemps().newTemp();

                    sb.append("  ").append(tmpStruct)
                            .append(" = call %String* @createString(i8* getelementptr ([").append(len)
                            .append(" x i8], [").append(len).append(" x i8]* ").append(strName)
                            .append(", i32 0, i32 0))\n");
                    sb.append("  ret %String* ").append(tmpStruct).append("\n");

                }
                else if (retInfo.isList()) {
                    String exprCode = ret.expr.accept(visitor);
                    sb.append(exprCode);
                    String tmpVal = extractLastVal(exprCode);
                    String tmpType = extractLastType(exprCode);
                    sb.append("  ret ").append(tmpType).append(" ").append(tmpVal).append("\n");
                } else {
                    sb.append(ret.accept(visitor));
                }
            } else {
                sb.append(stmt.accept(visitor));
            }
        }

        if ("void".equals(llvmRetType) && !containsReturn(fn)) {
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }

    private boolean containsReturn(FunctionNode fn) {
        return fn.getBody().stream().anyMatch(n -> n instanceof ReturnNode);
    }

    private String extractLastVal(String code) {
        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";
        int t = code.indexOf(";;TYPE:", v);
        return (t == -1) ? "" : code.substring(v + 6, t).trim();
    }

    private String extractLastType(String code) {
        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "";
        int end = code.indexOf("\n", t);
        if (end == -1) end = code.length();
        return code.substring(t + 7, end).trim();
    }
}
