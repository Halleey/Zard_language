package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.variables.LiteralNode;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.List;
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

        visitor.registerFunctionType(fn.getName(),
                new TypeInfos("any", "void", null));

        String declaredType = fn.getReturnType();

        TypeInfos retInfo;
        if ("void".equals(declaredType) && containsReturn(fn)) {

            visitor.getCallEmitter().markBeingDeduced(fn.getName());
            retInfo = returnInferer.deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(fn.getName());
        } else {
            String llvm = typeMapper.toLLVM(declaredType);
            String elem = null;
            if (declaredType.startsWith("List<") && declaredType.endsWith(">")) {
                elem = declaredType.substring(5, declaredType.length() - 1);
            }
            retInfo = new TypeInfos(declaredType, llvm, elem);
        }

        visitor.registerFunctionType(fn.getName(), retInfo);

        String llvmRetType = retInfo.getLLVMType();

        List<String> paramSignatures = new ArrayList<>();
        for (int i = 0; i < fn.getParams().size(); i++) {
            String paramSource = fn.getParamTypes().get(i);
            String llvmType = typeMapper.toLLVM(paramSource);
            paramSignatures.add(llvmType + " %" + fn.getParams().get(i));

            visitor.putVarType(fn.getParams().get(i),
                    new TypeInfos(paramSource, llvmType, null));
        }

        sb.append("; === Função: ").append(fn.getName()).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(fn.getName())
                .append("(").append(String.join(", ", paramSignatures)).append(") {\nentry:\n");

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

                    String tmpPtr = visitor.getTemps().newTemp();
                    String tmpMalloc = visitor.getTemps().newTemp();
                    String tmpStruct = visitor.getTemps().newTemp();
                    String tmpFieldData = visitor.getTemps().newTemp();
                    String tmpFieldLen = visitor.getTemps().newTemp();

                    sb.append("  ").append(tmpPtr)
                            .append(" = getelementptr inbounds [").append(len)
                            .append(" x i8], [").append(len)
                            .append(" x i8]* ").append(strName)
                            .append(", i32 0, i32 0\n");

                    sb.append("  ").append(tmpMalloc)
                            .append(" = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))\n");
                    sb.append("  ").append(tmpStruct)
                            .append(" = bitcast i8* ").append(tmpMalloc).append(" to %String*\n");

                    sb.append("  ").append(tmpFieldData)
                            .append(" = getelementptr inbounds %String, %String* ")
                            .append(tmpStruct).append(", i32 0, i32 0\n");
                    sb.append("  store i8* ").append(tmpPtr).append(", i8** ").append(tmpFieldData).append("\n");

                    sb.append("  ").append(tmpFieldLen)
                            .append(" = getelementptr inbounds %String, %String* ")
                            .append(tmpStruct).append(", i32 0, i32 1\n");
                    sb.append("  store i64 ").append(len - 1).append(", i64* ").append(tmpFieldLen).append("\n");

                    sb.append("  ret %String* ").append(tmpStruct).append("\n");
                }
                else if (retInfo.isList()) {
                    String exprCode = ret.expr.accept(visitor);
                    sb.append(exprCode);

                    String tmpVal = extractLastVal(exprCode);
                    String tmpType = extractLastType(exprCode);

                    sb.append("  ret ").append(tmpType).append(" ").append(tmpVal).append("\n");
                }
                else {
                    sb.append(ret.accept(visitor));
                }
            }
            else {
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
