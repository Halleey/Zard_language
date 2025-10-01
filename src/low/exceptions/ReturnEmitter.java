package low.exceptions;

import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.functions.ReturnTypeInferer;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.HashMap;
public class ReturnEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;

    public ReturnEmitter(TempManager temps, LLVisitorMain visitor,
                         TypeMapper typeMapper, ReturnTypeInferer returnInferer) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public String emit(ReturnNode node) {
        if (node.expr == null) {
            return "  ret void\n";
        }

        // Caso retorno de literal string
        if (node.expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            String str = (String) lit.value.getValue();
            String strName = visitor.getGlobalStrings().getOrCreateString(str);
            int len = str.length() + 1;
            return "  ret i8* getelementptr inbounds ([" + len + " x i8], [" + len + " x i8]* "
                    + strName + ", i32 0, i32 0)\n";
        }

        // Avalia expressão normalmente
        String exprLLVM = node.expr.accept(visitor);
        String valueTemp = extractTemp(exprLLVM);
        String exprType = extractType(exprLLVM);

        // Descobre tipo de retorno da função atual usando a última função registrada
        String currentFunctionType = "i32"; // fallback
        if (!visitor.functionTypes.isEmpty()) {
            // pega a última função registrada (assumindo que emitimos returns na função certa)
            currentFunctionType = new ArrayList<>(visitor.functionTypes.values())
                    .get(visitor.functionTypes.size() - 1);
        }

        // Se a função retorna double mas a expressão é int, converte
        if ("double".equals(currentFunctionType) && "i32".equals(exprType)) {
            String convTemp = temps.newTemp();
            exprLLVM += "  " + convTemp + " = sitofp i32 " + valueTemp + " to double\n;;VAL:" + convTemp + ";;TYPE:double\n";
            valueTemp = convTemp;
            exprType = "double";
        }

        // Se a função retorna int mas a expressão é double, converte
        if ("i32".equals(currentFunctionType) && "double".equals(exprType)) {
            String convTemp = temps.newTemp();
            exprLLVM += "  " + convTemp + " = fptosi double " + valueTemp + " to i32\n;;VAL:" + convTemp + ";;TYPE:i32\n";
            valueTemp = convTemp;
        }

        return exprLLVM + "  ret " + currentFunctionType + " " + valueTemp + "\n";
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int typeIdx = code.lastIndexOf(";;TYPE:");
        if (typeIdx == -1) throw new RuntimeException("Não encontrou ;;TYPE: em: " + code);
        return code.substring(typeIdx + 7).trim();
    }
}
