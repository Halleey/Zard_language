package low;

import expressions.TypedValue;

import java.util.Map;


public class VariableEmitter {
    private final Map<String, String> varTypes;
    private final TempManager temps;

    public VariableEmitter(Map<String, String> varTypes, TempManager temps) {
        this.varTypes = varTypes;
        this.temps = temps;
    }

    public String emitDeclaration(String name, TypedValue value) {
        String llvmType = switch (value.getType()) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string" -> "i8*";
            default -> "i32";
        };
        varTypes.put(name, llvmType);

        StringBuilder llvm = new StringBuilder();
        llvm.append("  %").append(name).append(" = alloca ").append(llvmType).append("\n");

        if (value != null) {
            switch (value.getType()) {
                case "int" -> llvm.append("  store i32 ").append(value.getValue()).append(", i32* %").append(name).append("\n");
                case "double" -> llvm.append("  store double ").append(value.getValue()).append(", double* %").append(name).append("\n");
                case "boolean" -> llvm.append("  store i1 ").append(((Boolean)value.getValue() ? "1" : "0")).append(", i1* %").append(name).append("\n");
                case "string" -> {
                    // tratar via GlobalStringManager
                }
            }
        }

        return llvm.toString();
    }

    public String emitLoad(String name) {
        String type = varTypes.getOrDefault(name, "i32");
        String tmp = temps.newTemp();
        return "  " + tmp + " = load " + type + ", " + type + "* %" + name + "\n"
                + ";;VAL:" + tmp + ";;TYPE:" + type;
    }
}
