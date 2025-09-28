package low.exceptions;

import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.functions.ReturnTypeInferer;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;

import java.util.HashMap;

public class ReturnEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper;
    private final ReturnTypeInferer returnInferer;

    public ReturnEmitter(TempManager temps, LLVisitorMain visitor,
                         TypeMapper typeMapper, ReturnTypeInferer returnInferer) {
        this.temps = temps;
        this.visitor = visitor;
        this.typeMapper = typeMapper;
        this.returnInferer = returnInferer;
    }

    public String emit(ReturnNode node) {
        if (node.expr == null) {
            return "  ret void\n";
        }

        // Caso retorno de literal string
        if (node.expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            String str = (String) lit.value.getValue();
            String strName = visitor.getGlobalStrings().getOrCreateString(str);
            int len = str.length() + 1; // inclui \0
            return "  ret i8* getelementptr inbounds ([" + len + " x i8], [" + len + " x i8]* "
                    + strName + ", i32 0, i32 0)\n";
        }

        String llvmType;
        if (node.expr instanceof FunctionCallNode callNode) {
            llvmType = visitor.getFunctionType(callNode.getName());

            if (llvmType == null || "any".equals(llvmType)) {
                if (visitor.getCallEmitter().isBeingDeduced(callNode.getName())) {
                    llvmType = "i32";
                } else {

                    llvmType = returnInferer.inferType(callNode, new HashMap<>());
                }
            }

            llvmType = typeMapper.toLLVM(llvmType);
        }

        else {
            // Avalia expressão normalmente
            String exprLLVM = node.expr.accept(visitor);
            String valueTemp = extractTemp(exprLLVM);
            String exprType = extractType(exprLLVM);
            return exprLLVM + "  ret " + exprType + " " + valueTemp + "\n";
        }

        // Avalia a expressão normalmente
        String exprLLVM = node.expr.accept(visitor);
        String valueTemp = extractTemp(exprLLVM);

        return exprLLVM + "  ret " + llvmType + " " + valueTemp + "\n";
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
