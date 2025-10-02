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
            System.out.println("[DEBUG] Return void");
            return "  ret void\n";
        }

        if (node.expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            String str = (String) lit.value.getValue();
            String strName = visitor.getGlobalStrings().getOrCreateString(str);
            int len = str.length() + 1;
            System.out.println("[DEBUG] Return string literal: " + str);
            return "  ret i8* getelementptr inbounds ([" + len + " x i8], [" + len + " x i8]* "
                    + strName + ", i32 0, i32 0)\n";
        }

        System.out.println("[DEBUG] Avaliando expressão de return: " + node.expr.getClass().getSimpleName());

        String exprLLVM = node.expr.accept(visitor);

        System.out.println("[DEBUG] LLVM gerado pela expressão:\n" + exprLLVM);

        String valueTemp = extractTemp(exprLLVM);
        String exprType = extractType(exprLLVM);

        System.out.println("[DEBUG] Temp extraído: " + valueTemp + ", Tipo extraído: " + exprType);

        String currentFunctionType = "i32";
        if (!visitor.functionTypes.isEmpty()) {
            currentFunctionType = new ArrayList<>(visitor.functionTypes.values())
                    .get(visitor.functionTypes.size() - 1);
        }

        // conversões automáticas se necessário
        if ("double".equals(currentFunctionType) && "i32".equals(exprType)) {
            String convTemp = temps.newTemp();
            exprLLVM += "  " + convTemp + " = sitofp i32 " + valueTemp + " to double\n;;VAL:" + convTemp + ";;TYPE:double\n";
            valueTemp = convTemp;
            exprType = "double";
            System.out.println("[DEBUG] Convertendo i32 para double, novo temp: " + valueTemp);
        }

        if ("i32".equals(currentFunctionType) && "double".equals(exprType)) {
            String convTemp = temps.newTemp();
            exprLLVM += "  " + convTemp + " = fptosi double " + valueTemp + " to i32\n;;VAL:" + convTemp + ";;TYPE:i32\n";
            valueTemp = convTemp;
            exprType = "i32";
            System.out.println("[DEBUG] Convertendo double para i32, novo temp: " + valueTemp);
        }

        System.out.println("[DEBUG] Return final: tipo=" + currentFunctionType + ", temp=" + valueTemp);

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
        int newlineIdx = code.indexOf("\n", typeIdx);
        return code.substring(typeIdx + 7, newlineIdx == -1 ? code.length() : newlineIdx).trim();
    }
}
