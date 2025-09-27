package low.exceptions;

import ast.exceptions.ReturnNode;
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
        // Avalia a expressão do return
        String exprLLVM = node.expr.accept(visitor);

        // Extrai valor e tipo do ;;VAL: e ;;TYPE:
        String valueTemp = extractTemp(exprLLVM);
        String llvmType = extractType(exprLLVM);

        // Monta o LLVM final
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
