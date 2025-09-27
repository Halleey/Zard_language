package low.functions;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import low.TempManager;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.List;


public class FunctionCallEmitter {
    private final TempManager temps;

    public FunctionCallEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(FunctionCallNode node, LLVisitorMain visitor) {
        StringBuilder sb = new StringBuilder();
        List<String> llvmArgs = new ArrayList<>();

        // Avalia argumentos
        for (ASTNode arg : node.getArgs()) {
            String argLLVM = arg.accept(visitor);
            String temp = extractTemp(argLLVM);
            String type = extractType(argLLVM);

            llvmArgs.add(type + " " + temp);
            sb.append(argLLVM);
        }

        String retTemp = temps.newTemp();
        String retType = "i32"; // ajustar se função for dinâmica

        sb.append("  ").append(retTemp)
                .append(" = call ").append(retType)
                .append(" @").append(node.getName())
                .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                .append(";;VAL:").append(retTemp).append(";;TYPE:").append(retType).append("\n");

        return sb.toString();
    }

    private String extractTemp(String code) {
        int valIdx = code.lastIndexOf(";;VAL:");
        if (valIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", valIdx);
        return code.substring(valIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int typeIdx = code.lastIndexOf(";;TYPE:");
        if (typeIdx == -1) throw new RuntimeException("Não encontrou ;;TYPE: em: " + code);
        return code.substring(typeIdx + 7).trim();
    }
}