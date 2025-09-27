package low.exceptions;

import ast.exceptions.ReturnNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.module.LLVisitorMain;

public class ReturnEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;

    public ReturnEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public String emit(ReturnNode node) {
        // Caso retorno de literal string
        if (node.expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            String str = (String) lit.value.getValue();
            String strName = visitor.getGlobalStrings().getOrCreateString(str);
            int len = str.length() + 1; // inclui \0
            return "  ret i8* getelementptr inbounds ([" + len + " x i8], [" + len + " x i8]* "
                    + strName + ", i32 0, i32 0)\n";
        }

        // Avalia expressão normalmente
        String exprLLVM = node.expr.accept(visitor);

        // Extrai valor e tipo do ;;VAL: e ;;TYPE:
        String valueTemp = extractTemp(exprLLVM);
        String llvmType = extractType(exprLLVM);

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
