package low.variables.exps;

import ast.expressions.CompoundAssignmentNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;
import low.variables.VariableEmitter;

import java.util.Map;
public class CompoundAssignmentEmitter {

    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final VariableEmitter varEmitter;
    private final LLVMEmitVisitor visitor;

    public CompoundAssignmentEmitter(
            Map<String, TypeInfos> varTypes,
            TempManager temps,
            VariableEmitter varEmitter,
            LLVMEmitVisitor visitor
    ) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.varEmitter = varEmitter;
        this.visitor = visitor;
    }

    public LLVMValue emit(CompoundAssignmentNode node) {

        StringBuilder llvm = new StringBuilder();

        String varName = node.getTarget().getName();

        TypeInfos info = varTypes.get(varName);
        if (info == null) {
            throw new RuntimeException("Tipo não encontrado para variável: " + varName);
        }

        LLVMTYPES llvmType = info.getLLVMType();

        String ptr = varEmitter.getVarPtr(varName);
        if (ptr == null) {
            throw new RuntimeException("Ponteiro não encontrado para variável: " + varName);
        }

        String oldVal = temps.newTemp();

        llvm.append("  ").append(oldVal)
                .append(" = load ").append(llvmType)
                .append(", ").append(llvmType).append("* ").append(ptr).append("\n");

        LLVMValue rhsVal = node.getExpr().accept(visitor);
        llvm.append(rhsVal.getCode());

        if (!rhsVal.getType().getClass().equals(llvmType.getClass())) {
            throw new RuntimeException(
                    "Tipos incompatíveis em " + node.getOperator()
                            + ": esperado " + llvmType + ", encontrado " + rhsVal.getType()
            );
        }

        String result = temps.newTemp();

        boolean isInt = llvmType instanceof LLVMInt;
        boolean isDouble = llvmType instanceof LLVMDouble;

        switch (node.getOperator()) {
            case "+=" -> {
                if (isInt) {
                    llvm.append("  ").append(result)
                            .append(" = add i32 ")
                            .append(oldVal).append(", ").append(rhsVal.getName()).append("\n");
                } else if (isDouble) {
                    llvm.append("  ").append(result)
                            .append(" = fadd double ")
                            .append(oldVal).append(", ").append(rhsVal.getName()).append("\n");
                } else {
                    throw new RuntimeException("Tipo não suportado para +=: " + llvmType);
                }
            }

            case "-=" -> {
                if (isInt) {
                    llvm.append("  ").append(result)
                            .append(" = sub i32 ")
                            .append(oldVal).append(", ").append(rhsVal.getName()).append("\n");
                } else if (isDouble) {
                    llvm.append("  ").append(result)
                            .append(" = fsub double ")
                            .append(oldVal).append(", ").append(rhsVal.getName()).append("\n");
                } else {
                    throw new RuntimeException("Tipo não suportado para -=: " + llvmType);
                }
            }

            default -> throw new RuntimeException(
                    "Operador composto não suportado: " + node.getOperator()
            );
        }

        llvm.append("  store ").append(llvmType).append(" ").append(result)
                .append(", ").append(llvmType).append("* ").append(ptr).append("\n");

        return new LLVMValue(llvmType, result, llvm.toString());
    }
}