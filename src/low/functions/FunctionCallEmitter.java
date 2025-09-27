package low.functions;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import low.TempManager;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.List;
public class FunctionCallEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;

    public FunctionCallEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public String emit(FunctionCallNode node) {
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

        // Deduz tipo de retorno a partir do FunctionNode
        FunctionNode fn = visitor.getFunctionNode(node.getName());
        String fnReturnType = (fn != null) ? fn.getReturnType() : "void";
        String llvmRetType = mapType(fnReturnType);

        if (llvmRetType.equals("void")) {
            // Função void: chama direto, sem atribuir
            sb.append("  call void @").append(node.getName())
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n");
        } else {
            // Função com retorno: cria temp
            String retTemp = temps.newTemp();
            sb.append("  ").append(retTemp)
                    .append(" = call ").append(llvmRetType)
                    .append(" @").append(node.getName())
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                    .append(";;VAL:").append(retTemp).append(";;TYPE:").append(llvmRetType).append("\n");
        }

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

    private String mapType(String type) {
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string", "list", "var" -> "i8*";
            case "void" -> "void";
            default -> throw new RuntimeException("Tipo não suportado: " + type);
        };
    }
}
