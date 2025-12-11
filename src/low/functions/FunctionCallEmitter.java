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
        System.out.println("[FCall] markBeingDeduced: " + functionName);
        beingDeduced.add(functionName);
        System.out.println("[FCall] beingDeduced agora = " + beingDeduced);
    }

    public void unmarkBeingDeduced(String functionName) {
        System.out.println("[FCall] unmarkBeingDeduced: " + functionName);
        beingDeduced.remove(functionName);
        System.out.println("[FCall] beingDeduced agora = " + beingDeduced);
    }

    public boolean isBeingDeduced(String functionName) {
        boolean contains = beingDeduced.contains(functionName);
        System.out.println("[FCall] isBeingDeduced(" + functionName + ") = " + contains);
        return contains;
    }

    private String resolveLLVMFuncName(String funcName, LLVisitorMain visitor) {
        System.out.println("[FCall] resolveLLVMFuncName para: " + funcName);

        String[] parts = funcName.split("\\.");

        if (parts.length == 2) {
            String alias = parts[0];
            String base = parts[1];

            String full = alias + "_" + base;
            System.out.println("[FCall]  tentando alias+base: " + full);

            if (visitor.getFunctionType(full) != null) {
                System.out.println("[FCall]  achou functionType para alias+base: " + full);
                return full;
            }
        }

        String replaced = funcName.replace('.', '_');
        System.out.println("[FCall]  tentando com dots->underscore: " + replaced);
        if (visitor.getFunctionType(replaced) != null) {
            System.out.println("[FCall]  achou functionType para replaced: " + replaced);
            return replaced;
        }

        String fallback = parts[parts.length - 1];
        System.out.println("[FCall]  fallback para último pedaço: " + fallback);
        return fallback;
    }

    private void debugDumpFunctions(LLVisitorMain visitor) {
        System.out.println("[FCall] ==== DUMP visitor.functions ====");
        if (visitor.functions == null || visitor.functions.isEmpty()) {
            System.out.println("[FCall]  (vazio)");
        } else {
            for (String k : visitor.functions.keySet()) {
                System.out.println("[FCall]  key=" + k + " -> FunctionNode=" + visitor.functions.get(k));
            }
        }

        System.out.println("[FCall] ==== DUMP visitor.importedFunctions ====");
        if (visitor.importedFunctions == null || visitor.importedFunctions.isEmpty()) {
            System.out.println("[FCall]  (vazio)");
        } else {
            for (String k : visitor.importedFunctions.keySet()) {
                System.out.println("[FCall]  key=" + k + " -> FunctionNode=" + visitor.importedFunctions.get(k));
            }
        }
    }

    public String emit(FunctionCallNode node, LLVisitorMain visitor) {
        StringBuilder sb = new StringBuilder();
        List<String> llvmArgs = new ArrayList<>();
        TypeMapper typeMapper = new TypeMapper();

        String originalName = node.getName();
        System.out.println("==========================================");
        System.out.println("[FCall] emit() INÍCIO");
        System.out.println("[FCall] node.getName() = " + originalName);
        System.out.println("[FCall] quantidade de args = " + node.getArgs().size());

        String llvmFuncName = resolveLLVMFuncName(originalName, visitor);
        System.out.println("[FCall] llvmFuncName resolvido: " + llvmFuncName);

        FunctionNode targetFn;

        System.out.println("[FCall] tentando visitor.functions.get(" + originalName + ")");
        targetFn = visitor.functions.get(originalName);
        if (targetFn != null) {
            System.out.println("[FCall] alvo encontrado em functions (originalName): " + originalName);
        } else {
            System.out.println("[FCall] tentando visitor.functions.get(" + llvmFuncName + ")");
            targetFn = visitor.functions.get(llvmFuncName);
            if (targetFn != null) {
                System.out.println("[FCall] alvo encontrado em functions (llvmName): " + llvmFuncName);
            }
        }

        if (targetFn == null) {
            // 3) tentar importadas
            System.out.println("[FCall] tentando importedFunctions.get(" + originalName + ")");
            targetFn = visitor.importedFunctions.get(originalName);
            if (targetFn != null) {
                System.out.println("[FCall] alvo encontrado em importedFunctions (originalName): " + originalName);
            } else {
                System.out.println("[FCall] tentando importedFunctions.get(" + llvmFuncName + ")");
                targetFn = visitor.importedFunctions.get(llvmFuncName);
                if (targetFn != null) {
                    System.out.println("[FCall] alvo encontrado em importedFunctions (llvmName): " + llvmFuncName);
                }
            }
        }

        if (targetFn == null) {
            System.out.println("[FCall] targetFn CONTINUA null para " + originalName + "/" + llvmFuncName);
            debugDumpFunctions(visitor);
        } else {
            System.out.println("[FCall] targetFn FINAL = " + targetFn.getName()
                    + " implStruct=" + targetFn.getImplStructName());
        }

        List<ParamInfo> expectedParams =
                targetFn != null ? targetFn.getParameters() : null;

        if (expectedParams != null) {
            System.out.println("[FCall] parâmetros esperados:");
            for (int i = 0; i < expectedParams.size(); i++) {
                ParamInfo p = expectedParams.get(i);
                System.out.println("  idx=" + i + " name=" + p.name() +
                        " type=" + p.type() + " ref=" + p.isRef());
            }
        } else {
            System.out.println("[FCall] expectedParams == null para " + originalName);
        }

        for (int i = 0; i < node.getArgs().size(); i++) {

            ASTNode arg = node.getArgs().get(i);
            ParamInfo param =
                    (expectedParams != null && i < expectedParams.size())
                            ? expectedParams.get(i)
                            : null;

            System.out.println("[FCall] Arg idx=" + i +
                    " node=" + arg.getClass().getSimpleName() +
                    " param=" + (param != null ? param.name() : "null") +
                    " ref=" + (param != null && param.isRef()));

            if (param != null && param.isRef()) {

                if (!(arg instanceof VariableNode var)) {
                    throw new RuntimeException(
                            "Parâmetro '&" + param.name() + "' exige uma variável"
                    );
                }

                String varName = var.getName();
                System.out.println("[FCall] &param – trabalhando com varName=" + varName);

                String varPtr = visitor
                        .getVariableEmitter()
                        .getVarPtr(varName);

                System.out.println("[FCall]  getVarPtr(" + varName + ") -> " + varPtr);

                TypeInfos info = visitor.getVarType(varName);
                if (info == null) {
                    throw new RuntimeException("Tipo não registrado para variável: " + varName);
                }

                String valueLLVMType = info.getLLVMType();
                String paramLLVMType = valueLLVMType + "*";

                System.out.println("[FCall] &param: varName=" + varName +
                        " valueLLVMType=" + valueLLVMType +
                        " passando como " + paramLLVMType + " " + varPtr);

                llvmArgs.add(paramLLVMType + " " + varPtr);
                continue;
            }

            System.out.println("[FCall] parâmetro normal, emitindo arg idx=" + i);
            String argLLVM = arg.accept(visitor);
            sb.append(argLLVM);

            String temp = extractTemp(argLLVM);
            String argType = extractType(argLLVM); // já é LLVM type (ex: i32, double, %String*)

            System.out.println("[FCall] valor arg idx=" + i +
                    " temp=" + temp +
                    " type=" + argType);

            String expectedLLVMType = null;
            if (param != null && param.type() != null) {
                expectedLLVMType = typeMapper.toLLVM(param.type());
                System.out.println("[FCall] expectedLLVMType para idx=" + i + " = " + expectedLLVMType);
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

                System.out.println("[FCall] conversão implícita i32 -> double em idx=" + i +
                        " novoTemp=" + conv);
            }

            llvmArgs.add(argType + " " + temp);
        }

        System.out.println("[FCall] consultando getFunctionType(" + llvmFuncName + ")");
        TypeInfos retInfo = visitor.getFunctionType(llvmFuncName);
        if (retInfo == null) {
            System.out.println("[FCall] getFunctionType(" + llvmFuncName + ") retornou null!");
            debugDumpFunctions(visitor);
            throw new RuntimeException("Função não registrada: " + originalName);
        }

        String retType = retInfo.getLLVMType();
        System.out.println("[FCall] retType LLVM = " + retType);

        if ("void".equals(retType)) {
            System.out.println("[FCall] Emitindo call void @" + llvmFuncName);
            sb.append("  call void @")
                    .append(llvmFuncName)
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                    .append(";;VAL:void;;TYPE:void\n");
        } else {
            System.out.println("[FCall] Emitindo call com retorno @" + llvmFuncName);
            String retTemp = visitor.getTemps().newTemp();
            sb.append("  ").append(retTemp)
                    .append(" = call ").append(retType).append(" @")
                    .append(llvmFuncName)
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                    .append(";;VAL:").append(retTemp)
                    .append(";;TYPE:").append(retType).append("\n");
        }

        System.out.println("[FCall] emit() FIM para " + originalName);
        System.out.println("==========================================");
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
