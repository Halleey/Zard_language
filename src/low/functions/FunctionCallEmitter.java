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
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMVoid;

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

    public LLVMValue emit(FunctionCallNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();
        List<String> llvmArgs = new ArrayList<>();

        String originalName = node.getName();
        String llvmFuncName = resolveLLVMFuncName(originalName, visitor);

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
            ParamInfo param = (expectedParams != null && i < expectedParams.size())
                    ? expectedParams.get(i)
                    : null;

            if (param != null && param.isRef()) {
                if (!(arg instanceof VariableNode var)) {
                    throw new RuntimeException("Parâmetro '&" + param.name() + "' exige variável");
                }

                String varPtr = visitor.getVariableEmitter().getVarPtr(var.getName());
                LLVMTYPES varType = visitor.getVarType(var.getName()).getLLVMType();

                llvmArgs.add(varType + "* " + varPtr);
                continue;
            }

            LLVMValue argVal = arg.accept(visitor);
            llvm.append(argVal.getCode());

            String temp = argVal.getName();
            LLVMTYPES argType = argVal.getType();

            if (param != null && param.type() != null) {
                Type expectedType = param.type();

                if (arg.getType() instanceof PrimitiveTypes argPrim &&
                        expectedType instanceof PrimitiveTypes expPrim) {

                    if (argPrim == PrimitiveTypes.INT && expPrim == PrimitiveTypes.DOUBLE) {
                        String convTemp = temps.newTemp();

                        llvm.append("  ").append(convTemp)
                                .append(" = sitofp i32 ").append(temp)
                                .append(" to double\n");

                        temp = convTemp;
                        argType = new LLVMDouble();
                    }
                }
            }

            llvmArgs.add(argType + " " + temp);
        }

        TypeInfos retInfo = visitor.getFunctionType(llvmFuncName);

        if (retInfo == null) {
            debugDumpFunctions(visitor);
            throw new RuntimeException("Função não registrada: " + originalName);
        }

        LLVMTYPES retType = retInfo.getLLVMType();

        if (retType instanceof LLVMVoid) {
            llvm.append("  call void @").append(llvmFuncName)
                    .append("(").append(String.join(", ", llvmArgs)).append(")\n");

            return new LLVMValue(new LLVMVoid(), "", llvm.toString());
        }

        String retTemp = temps.newTemp();

        llvm.append("  ").append(retTemp)
                .append(" = call ").append(retType)
                .append(" @").append(llvmFuncName)
                .append("(").append(String.join(", ", llvmArgs)).append(")\n");

        return new LLVMValue(retType, retTemp, llvm.toString());
    }

}
