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
        TypeMapper typeMapper = new TypeMapper();

        for (ASTNode arg : node.getArgs()) {
            String argLLVM = arg.accept(visitor);
            String temp = extractTemp(argLLVM);
            String type = typeMapper.toLLVM(extractType(argLLVM));

            if ("string".equals(type)) type = "%String*";

            llvmArgs.add(type + " " + temp);
            sb.append(argLLVM);
        }

        String funcName = node.getName();                  // e.g. "math.factorial" or "factorial"
        String llvmFuncName = funcName.replace('.', '_');  // símbolo LLVM usado nas chamadas

        // --- RESILIENT LOOKUP: tenta múltiplas chaves ---
        String retType = null;
        // 1) exato: "math.factorial"
        retType = visitor.getFunctionType(funcName);
        // 2) com underline: "math_factorial"
        if (retType == null) retType = visitor.getFunctionType(funcName.replace('.', '_'));
        // 3) simples: "factorial"
        if (retType == null && funcName.contains(".")) {
            String simpleName = funcName.substring(funcName.indexOf('.') + 1);
            retType = visitor.getFunctionType(simpleName);
            // 3b) simples com underscore (salvo por segurança)
            if (retType == null) retType = visitor.getFunctionType(simpleName.replace('.', '_'));
        }
        // 4) fallback final: try name with '.' replaced by '_' again just in case
        if (retType == null) retType = visitor.getFunctionType(funcName.replace('.', '_'));

        if (retType == null)
            throw new RuntimeException("Função não registrada: " + funcName);

        if ("any".equals(retType) && beingDeduced.contains(funcName)) {
            retType = "i32";
        }

        retType = typeMapper.toLLVM(retType);
        if ("string".equals(retType)) retType = "%String*";

        if ("void".equals(retType)) {
            sb.append("  call void @")
                    .append(llvmFuncName)
                    .append("(")
                    .append(String.join(", ", llvmArgs))
                    .append(")\n")
                    .append(";;VAL:void;;TYPE:void\n");
        } else {
            String retTemp = temps.newTemp();
            sb.append("  ").append(retTemp)
                    .append(" = call ").append(retType)
                    .append(" @").append(llvmFuncName)
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                    .append(";;VAL:").append(retTemp)
                    .append(";;TYPE:").append(retType).append("\n");
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
}
