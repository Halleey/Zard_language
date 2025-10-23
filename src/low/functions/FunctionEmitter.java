package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.variables.LiteralNode;
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

        // registra função inicialmente com tipo provisório "any"
        visitor.registerFunctionType(fn.getName(), "any");

        // deduz tipo de retorno se necessário
        String retType = fn.getReturnType();
        if ("void".equals(retType) && containsReturn(fn)) {
            visitor.getCallEmitter().markBeingDeduced(fn.getName());
            retType = returnInferer.deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(fn.getName());
        }

        String llvmRetType = typeMapper.toLLVM(retType);
        visitor.registerFunctionType(fn.getName(), llvmRetType);

        // assinatura LLVM
        List<String> paramSignatures = new ArrayList<>();
        for (int i = 0; i < fn.getParams().size(); i++) {
            String llvmType = typeMapper.toLLVM(fn.getParamTypes().get(i));
            paramSignatures.add(llvmType + " %" + fn.getParams().get(i));
        }

        sb.append("; === Função: ").append(fn.getName()).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(fn.getName())
                .append("(").append(String.join(", ", paramSignatures)).append(") {\nentry:\n");

        // Aloca e registra parâmetros
        for (int i = 0; i < fn.getParams().size(); i++) {
            String paramName = fn.getParams().get(i);
            String paramType = typeMapper.toLLVM(fn.getParamTypes().get(i));
            String paramPtr = "%" + paramName + "_addr";

            sb.append("  ").append(paramPtr).append(" = alloca ").append(paramType).append("\n");
            sb.append("  store ").append(paramType).append(" %").append(paramName)
                    .append(", ").append(paramType).append("* ").append(paramPtr).append("\n")
                    .append(";;VAL:").append(paramPtr).append(";;TYPE:").append(paramType).append("\n");

            visitor.getVariableEmitter().registerVarPtr(paramName, paramPtr);
            visitor.putVarType(paramName, paramType);
        }

        for (ASTNode stmt : fn.getBody()) {

            if (stmt instanceof ReturnNode ret && ret.expr instanceof LiteralNode lit &&
                    lit.value.getType().equals("string")) {

                String literal = (String) lit.value.getValue();

                String strName = visitor.getGlobalStrings().getOrCreateString(literal);
                int len = visitor.getGlobalStrings().getLength(literal);

                String tmpPtr = visitor.getTemps().newTemp();       // ponteiro para literal
                String tmpMalloc = visitor.getTemps().newTemp();    // i8* do malloc
                String tmpStruct = visitor.getTemps().newTemp();    // struct %String (heap)
                String tmpFieldData = visitor.getTemps().newTemp(); // campo .data
                String tmpFieldLen = visitor.getTemps().newTemp();  // campo .length

                // ponteiro para literal
                sb.append("  ").append(tmpPtr)
                        .append(" = getelementptr inbounds [").append(len)
                        .append(" x i8], [").append(len)
                        .append(" x i8]* ").append(strName)
                        .append(", i32 0, i32 0\n");

                // aloca struct %String no heap
                sb.append("  ").append(tmpMalloc)
                        .append(" = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))\n");
                sb.append("  ").append(tmpStruct)
                        .append(" = bitcast i8* ").append(tmpMalloc).append(" to %String*\n");

                // inicializa campo .data
                sb.append("  ").append(tmpFieldData)
                        .append(" = getelementptr inbounds %String, %String* ")
                        .append(tmpStruct).append(", i32 0, i32 0\n");
                sb.append("  store i8* ").append(tmpPtr).append(", i8** ").append(tmpFieldData).append("\n");

                // inicializa campo .length
                sb.append("  ").append(tmpFieldLen)
                        .append(" = getelementptr inbounds %String, %String* ")
                        .append(tmpStruct).append(", i32 0, i32 1\n");
                sb.append("  store i64 ").append(len - 1).append(", i64* ").append(tmpFieldLen).append("\n");

                // Retorno seguro (heap)
                sb.append("  ret %String* ").append(tmpStruct).append("\n");
            } else {
                sb.append(stmt.accept(visitor));
            }
        }

        // garante return void se não houver
        if ("void".equals(llvmRetType) && !containsReturn(fn)) {
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }

    private boolean containsReturn(FunctionNode fn) {
        return fn.getBody().stream().anyMatch(n -> n instanceof ReturnNode);
    }
}
