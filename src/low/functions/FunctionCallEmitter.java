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

    public String emit(FunctionCallNode node, LLVisitorMain visitor) {
        StringBuilder sb = new StringBuilder();
        List<String> llvmArgs = new ArrayList<>();
        TypeMapper typeMapper = new TypeMapper();

        FunctionNode targetFn = visitor.functions.get(node.getName());
        if (targetFn == null) {
            targetFn = visitor.importedFunctions.get(node.getName());
        }
        List<String> expectedParams = targetFn != null ? targetFn.getParamTypes() : null;

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

        String funcName = node.getName();
        String llvmFuncName = funcName.replace('.', '_');

        TypeInfos retInfo = visitor.getFunctionType(funcName);
        if (retInfo == null) retInfo = visitor.getFunctionType(funcName.replace('.', '_'));

        if (retInfo == null && funcName.contains(".")) {
            String simpleName = funcName.substring(funcName.indexOf('.') + 1);
            retInfo = visitor.getFunctionType(simpleName);
            if (retInfo == null) retInfo = visitor.getFunctionType(simpleName.replace('.', '_'));
        }

        if (retInfo == null) {
            throw new RuntimeException("Função não registrada: " + funcName);
        }

        String retType = retInfo.getLLVMType();

        if ("any".equals(retInfo.getSourceType()) && beingDeduced.contains(funcName)) {
            retType = "i32";
        }

        if ("string".equals(retType)) retType = "%String*";

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
