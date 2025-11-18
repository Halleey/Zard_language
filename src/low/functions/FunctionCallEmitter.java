package low.functions;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import low.TempManager;
import low.main.TypeInfos;
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
    private String resolveLLVMFuncName(String funcName, LLVisitorMain visitor) {
        // exemplo "math.cos" → ["math", "cos"]
        String[] parts = funcName.split("\\.");

        if (parts.length == 2) {
            String alias = parts[0];
            String base = parts[1];

            String full = alias + "_" + base;

            if (visitor.getFunctionType(full) != null) {
                return full;
            }
        }

        String replaced = funcName.replace('.', '_');
        if (visitor.getFunctionType(replaced) != null) {
            return replaced;
        }

        return parts[parts.length - 1];
    }
    public String emit(FunctionCallNode node, LLVisitorMain visitor) {
        StringBuilder sb = new StringBuilder();
        List<String> llvmArgs = new ArrayList<>();
        TypeMapper typeMapper = new TypeMapper();

        FunctionNode targetFn = visitor.functions.get(node.getName());
        if (targetFn == null) {
            targetFn = visitor.importedFunctions.get(node.getName());
        }

        List<String> expectedParams = targetFn != null ? targetFn.getParamTypes() : null;

        // Emitir argumentos
        for (int i = 0; i < node.getArgs().size(); i++) {
            ASTNode arg = node.getArgs().get(i);
            String argLLVM = arg.accept(visitor);
            String temp = extractTemp(argLLVM);
            String argType = typeMapper.toLLVM(extractType(argLLVM));

            if ("string".equals(argType)) argType = "%String*";

            sb.append(argLLVM);

            String expectedType = null;
            if (expectedParams != null && i < expectedParams.size()) {
                expectedType = typeMapper.toLLVM(expectedParams.get(i));
                if ("string".equals(expectedType)) expectedType = "%String*";
            }

            // Convertemos int → double quando esperado
            if (expectedType != null && !expectedType.equals(argType)) {
                if (argType.equals("i32") && expectedType.equals("double")) {
                    String conv = visitor.getTemps().newTemp();
                    sb.append("  ").append(conv).append(" = sitofp i32 ")
                            .append(temp).append(" to double\n")
                            .append(";;VAL:").append(conv).append(";;TYPE:double\n");
                    temp = conv;
                    argType = "double";
                }
            }

            llvmArgs.add(argType + " " + temp);
        }

        // Nome correto da função com alias
        String funcName = node.getName();
        String llvmFuncName = resolveLLVMFuncName(funcName, visitor);

        // Tipo de retorno
        TypeInfos retInfo = visitor.getFunctionType(llvmFuncName);
        if (retInfo == null) {
            throw new RuntimeException("Função não registrada: " + funcName);
        }

        String retType = retInfo.getLLVMType();
        if ("string".equals(retType)) retType = "%String*";

        // Void call
        if ("void".equals(retType)) {
            sb.append("  call void @")
                    .append(llvmFuncName)
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                    .append(";;VAL:void;;TYPE:void\n");
        } else {
            String retTemp = temps.newTemp();
            sb.append("  ").append(retTemp)
                    .append(" = call ").append(retType).append(" @")
                    .append(llvmFuncName).append("(")
                    .append(String.join(", ", llvmArgs)).append(")\n")
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
