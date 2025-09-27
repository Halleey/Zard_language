package low.functions;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import low.TempManager;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class FunctionCallEmitter {
    private final TempManager temps;
    private final Set<String> beingDeduced = new HashSet<>();

    public FunctionCallEmitter(TempManager temps) {
        this.temps = temps;
    }

    public void markBeingDeduced(String functionName) {
        beingDeduced.add(functionName);
    }

    public void unmarkBeingDeduced(String functionName) {
        beingDeduced.remove(functionName);
    }

    public boolean isBeingDeduced(String functionName) {
        return beingDeduced.contains(functionName);
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

        // Pega o tipo de retorno
        String retType = visitor.getFunctionType(node.getName());
        if (retType == null) throw new RuntimeException("Função não registrada: " + node.getName());

        // Se a função está sendo deduzida (recursiva), usa tipo provisório
        if ("any".equals(retType) && beingDeduced.contains(node.getName())) {
            retType = "i32"; // provisório
        }

        String retTemp = temps.newTemp();
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
