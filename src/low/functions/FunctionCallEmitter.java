package low.functions;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.variables.VariableNode;
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
        boolean contains = beingDeduced.contains(functionName);
        return contains;
    }

    private String resolveLLVMFuncName(String funcName, LLVisitorMain visitor) {

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

        String fallback = parts[parts.length - 1];
        return fallback;
    }

    private void debugDumpFunctions(LLVisitorMain visitor) {
        if (visitor.functions == null || visitor.functions.isEmpty()) {
        } else {
            for (String k : visitor.functions.keySet()) {
            }
        }

        if (visitor.importedFunctions == null || visitor.importedFunctions.isEmpty()) {
        } else {
            for (String k : visitor.importedFunctions.keySet()) {
            }
        }
    }

    public String emit(FunctionCallNode node, LLVisitorMain visitor) {
        StringBuilder sb = new StringBuilder();
        List<String> llvmArgs = new ArrayList<>();
        TypeMapper typeMapper = new TypeMapper();

        String originalName = node.getName();

        String llvmFuncName = resolveLLVMFuncName(originalName, visitor);

        FunctionNode targetFn;

        targetFn = visitor.functions.get(originalName);
        if (targetFn != null) {
        } else {
            targetFn = visitor.functions.get(llvmFuncName);
            if (targetFn != null) {
            }
        }

        if (targetFn == null) {
            targetFn = visitor.importedFunctions.get(originalName);
            if (targetFn != null) {
            } else {
                targetFn = visitor.importedFunctions.get(llvmFuncName);
                if (targetFn != null) {
                }
            }
        }

        if (targetFn == null) {
            debugDumpFunctions(visitor);
        }
        List<ParamInfo> expectedParams =
                targetFn != null ? targetFn.getParameters() : null;

        for (int i = 0; i < node.getArgs().size(); i++) {

            ASTNode arg = node.getArgs().get(i);
            ParamInfo param =
                    (expectedParams != null && i < expectedParams.size())
                            ? expectedParams.get(i)
                            : null;

            if (param != null && param.isRef()) {

                if (!(arg instanceof VariableNode var)) {
                    throw new RuntimeException(
                            "Parâmetro '&" + param.name() + "' exige uma variável"
                    );
                }

                String varName = var.getName();

                String varPtr = visitor
                        .getVariableEmitter()
                        .getVarPtr(varName);


                TypeInfos info = visitor.getVarType(varName);
                if (info == null) {
                    throw new RuntimeException("Tipo não registrado para variável: " + varName);
                }

                String valueLLVMType = info.getLLVMType();
                String paramLLVMType = valueLLVMType + "*";

                llvmArgs.add(paramLLVMType + " " + varPtr);
                continue;
            }

            String argLLVM = arg.accept(visitor);
            sb.append(argLLVM);

            String temp = extractTemp(argLLVM);
            String argType = extractType(argLLVM); // já é LLVM type (ex: i32, double, %String*)


            String expectedLLVMType = null;
            if (param != null && param.type() != null) {
                expectedLLVMType = typeMapper.toLLVM(param.type());
            }

            // conversão implícita i32 → double
            if (expectedLLVMType != null &&
                    "i32".equals(argType) &&
                    "double".equals(expectedLLVMType)) {

                String conv = visitor.getTemps().newTemp();
                sb.append("  ").append(conv)
                        .append(" = sitofp i32 ").append(temp)
                        .append(" to double\n")
                        .append(";;VAL:").append(conv)
                        .append(";;TYPE:double\n");

                temp = conv;
                argType = "double";

            }

            llvmArgs.add(argType + " " + temp);
        }

        TypeInfos retInfo = visitor.getFunctionType(llvmFuncName);
        if (retInfo == null) {
            debugDumpFunctions(visitor);
            throw new RuntimeException("Função não registrada: " + originalName);
        }

        String retType = retInfo.getLLVMType();

        if ("void".equals(retType)) {
            sb.append("  call void @")
                    .append(llvmFuncName)
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                    .append(";;VAL:void;;TYPE:void\n");
        } else {
            String retTemp = visitor.getTemps().newTemp();
            sb.append("  ").append(retTemp)
                    .append(" = call ").append(retType).append(" @")
                    .append(llvmFuncName)
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                    .append(";;VAL:").append(retTemp)
                    .append(";;TYPE:").append(retType).append("\n");
        }

        return sb.toString();
    }

    private String extractTemp(String ir) {

        int idx = ir.lastIndexOf(";;VAL:");
        if (idx < 0) return null;
        int end = ir.indexOf(";;TYPE:", idx);
        return ir.substring(idx + 6, end).trim();
    }

    private String extractType(String ir) {
        int idx = ir.lastIndexOf(";;TYPE:");
        if (idx < 0) return null;
        int end = ir.indexOf("\n", idx);
        if (end < 0) end = ir.length();
        return ir.substring(idx + 7, end).trim();
    }
}
