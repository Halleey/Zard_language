package low.functions;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.variables.VariableNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
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
        String[] parts = funcName.split("\\.");

        if (parts.length == 2) {
            String alias = parts[0];
            String base = parts[1];
            String full = alias + "_" + base;
            if (visitor.getFunctionType(full) != null) return full;
        }

        String replaced = funcName.replace('.', '_');
        if (visitor.getFunctionType(replaced) != null) return replaced;

        return parts[parts.length - 1];
    }

    private void debugDumpFunctions(LLVisitorMain visitor) {
        if (visitor.functions != null) visitor.functions.keySet().forEach(System.out::println);
        if (visitor.importedFunctions != null) visitor.importedFunctions.keySet().forEach(System.out::println);
    }

    public String emit(FunctionCallNode node, LLVisitorMain visitor) {
        StringBuilder sb = new StringBuilder();
        List<String> llvmArgs = new ArrayList<>();
        TypeMapper typeMapper = new TypeMapper();

        String originalName = node.getName();
        String llvmFuncName = resolveLLVMFuncName(originalName, visitor);

        // resolve target function
        FunctionNode targetFn = visitor.functions.getOrDefault(originalName,
                visitor.functions.getOrDefault(llvmFuncName,
                        visitor.importedFunctions.getOrDefault(originalName,
                                visitor.importedFunctions.get(llvmFuncName)
                        )));

        if (targetFn == null) {
            debugDumpFunctions(visitor);
            throw new RuntimeException("Função não registrada: " + originalName);
        }

        List<ParamInfo> expectedParams = targetFn.getParameters();

        for (int i = 0; i < node.getArgs().size(); i++) {
            ASTNode arg = node.getArgs().get(i);
            ParamInfo param = (expectedParams != null && i < expectedParams.size()) ? expectedParams.get(i) : null;

            if (param != null && param.isRef()) {
                if (!(arg instanceof VariableNode var)) {
                    throw new RuntimeException(
                            "Parâmetro '&" + param.name() + "' exige uma variável"
                    );
                }

                String varPtr = visitor.getVariableEmitter().getVarPtr(var.getName());
                Type varType = visitor.getVarType(var.getName()).getType(); // AST Type
                String llvmParamType = typeMapper.toLLVM(varType) + "*";
                llvmArgs.add(llvmParamType + " " + varPtr);
                continue;
            }

            // Avalia argumento
            String argLLVM = arg.accept(visitor);
            sb.append(argLLVM);

            String temp = extractTemp(argLLVM);
            String argLLVMType = extractType(argLLVM); // LLVM type (i32, double, %Struct*, etc.)

            // conversão implícita usando Type
            if (param != null && param.type() != null) {
                Type expectedType = param.type();

                if (arg.getType() instanceof PrimitiveTypes argPrim &&
                        expectedType instanceof PrimitiveTypes expPrim) {

                    if (argPrim == PrimitiveTypes.INT && expPrim == PrimitiveTypes.DOUBLE) {
                        String convTemp = temps.newTemp();
                        sb.append("  ").append(convTemp)
                                .append(" = sitofp i32 ").append(temp)
                                .append(" to double\n")
                                .append(";;VAL:").append(convTemp)
                                .append(";;TYPE:double\n");
                        temp = convTemp;
                        argLLVMType = "double";
                    }
                }
            }

            llvmArgs.add(argLLVMType + " " + temp);
        }

        TypeInfos retInfo = visitor.getFunctionType(llvmFuncName);
        if (retInfo == null) {
            debugDumpFunctions(visitor);
            throw new RuntimeException("Função não registrada: " + originalName);
        }

        String retLLVMType = retInfo.getLLVMType();
        if ("void".equals(retLLVMType)) {
            sb.append("  call void @").append(llvmFuncName)
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                    .append(";;VAL:void;;TYPE:void\n");
        } else {
            String retTemp = temps.newTemp();
            sb.append("  ").append(retTemp)
                    .append(" = call ").append(retLLVMType)
                    .append(" @").append(llvmFuncName)
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n")
                    .append(";;VAL:").append(retTemp)
                    .append(";;TYPE:").append(retLLVMType).append("\n");
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
