package low;

import expressions.TypedValue;
import java.util.Map;

public class AssignmentEmitter {
    private final Map<String, String> varTypes; // Reaproveitando o map do LLVisitorMain
    private final TempManager temps;

    public AssignmentEmitter(Map<String, String> varTypes, TempManager temps) {
        this.varTypes = varTypes;
        this.temps = temps;
    }

    public String emitAssignment(String varName, TypedValue value) {
        if (!varTypes.containsKey(varName)) {
            throw new RuntimeException("Variável não declarada: " + varName);
        }

        String llvmType = varTypes.get(varName);
        StringBuilder llvm = new StringBuilder();

        // Converte valor para string adequada
        String valStr;
        switch (llvmType) {
            case "i32":
                if (value.getType().equals("int")) {
                    valStr = value.getValue().toString();
                } else {
                    throw new RuntimeException("Atribuição inválida: tipo " + value.getType() + " para i32");
                }
                break;
            case "double":
                if (value.getType().equals("double")) {
                    valStr = value.getValue().toString();
                } else if (value.getType().equals("int")) {
                    valStr = ((Integer) value.getValue()).doubleValue() + "";
                } else {
                    throw new RuntimeException("Atribuição inválida: tipo " + value.getType() + " para double");
                }
                break;
            case "i1": // boolean
                if (value.getType().equals("boolean")) {
                    boolean b = (Boolean) value.getValue();
                    valStr = b ? "1" : "0";
                } else {
                    throw new RuntimeException("Atribuição inválida: tipo " + value.getType() + " para boolean");
                }
                break;
            case "i8*": // string
                if (value.getType().equals("string")) {
                    valStr = ""; // strings tratadas via GlobalStringManager
                } else {
                    throw new RuntimeException("Atribuição inválida: tipo " + value.getType() + " para string");
                }
                break;
            default:
                throw new RuntimeException("Tipo LLVM desconhecido: " + llvmType);
        }

        // Gera store
        if (!llvmType.equals("i8*")) { // strings são especiais
            llvm.append("  store ").append(llvmType).append(" ").append(valStr)
                    .append(", ").append(llvmType).append("* %").append(varName).append("\n");
        }

        return llvm.toString();
    }
}
