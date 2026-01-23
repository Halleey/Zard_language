package low.variables.exps;

import ast.expressions.CompoundAssignmentNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVMEmitVisitor;
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

    private String normalizeType(String llvmType) {
        return switch (llvmType) {
            case "int", "i32" -> "i32";
            case "double" -> "double";
            case "boolean", "bool", "i1" -> "i1";
            default -> llvmType;
        };
    }

    private String extractVal(String ir) {
        int v = ir.lastIndexOf(";;VAL:");
        int t = ir.lastIndexOf(";;TYPE:");
        if (v == -1 || t == -1)
            throw new RuntimeException("VAL/TYPE não encontrados no IR");
        return ir.substring(v + 6, t).trim();
    }

    private String extractType(String ir) {
        int t = ir.lastIndexOf(";;TYPE:");
        if (t == -1)
            throw new RuntimeException("TYPE não encontrado no IR");
        return ir.substring(t + 7).trim();
    }

    public String emit(CompoundAssignmentNode node) {
        StringBuilder llvm = new StringBuilder();

        String varName = node.getTarget().getName();

        TypeInfos info = varTypes.get(varName);
        if (info == null)
            throw new RuntimeException("Tipo não encontrado para variável: " + varName);

        String llvmType = normalizeType(info.getLLVMType());

        String ptr = varEmitter.getVarPtr(varName);
        if (ptr == null)
            throw new RuntimeException("Ponteiro não encontrado para variável: " + varName);

        String oldVal = temps.newTemp();
        llvm.append("  ").append(oldVal)
                .append(" = load ").append(llvmType)
                .append(", ").append(llvmType).append("* ").append(ptr).append("\n");

        String rhsIR = node.getExpr().accept(visitor);
        llvm.append(rhsIR);

        String rhsVal = extractVal(rhsIR);
        String rhsType = extractType(rhsIR);

        if (!rhsType.equals(llvmType)) {
            throw new RuntimeException(
                    "Tipos incompatíveis em " + node.getOperator()
                            + ": esperado " + llvmType + ", encontrado " + rhsType
            );
        }

        String result = temps.newTemp();
        boolean isInt = llvmType.equals("i32");

        switch (node.getOperator()) {
            case "+=" -> llvm.append("  ").append(result).append(" = ")
                    .append(isInt ? "add i32 " : "fadd double ")
                    .append(oldVal).append(", ").append(rhsVal).append("\n");

            case "-=" -> llvm.append("  ").append(result).append(" = ")
                    .append(isInt ? "sub i32 " : "fsub double ")
                    .append(oldVal).append(", ").append(rhsVal).append("\n");

            default -> throw new RuntimeException(
                    "Operador composto não suportado: " + node.getOperator()
            );
        }

        llvm.append("  store ").append(llvmType).append(" ").append(result)
                .append(", ").append(llvmType).append("* ").append(ptr).append("\n");

        llvm.append(";;VAL:").append(result)
                .append(";;TYPE:").append(llvmType).append("\n");

        return llvm.toString();
    }
}
