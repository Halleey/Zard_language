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
    public String emitDeclaration(String name, TypedValue value, String astType) {
        String type;
        Object val = null;

        if (value != null) {
            type = value.getType();
            val = value.getValue();
        } else {
            // valor nulo → usar o tipo da AST
            type = astType;
        }

        String llvmType = switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string" -> "i8*";
            default -> "i32";
        };

        varTypes.put(name, llvmType);

        StringBuilder llvm = new StringBuilder();
        llvm.append("  %").append(name).append(" = alloca ").append(llvmType).append("\n");

        if (val != null) {
            if (llvmType.equals("double") && val instanceof Integer) {
                val = ((Integer) val).doubleValue();
            }

            if (llvmType.equals("i1") && val instanceof Boolean) {
                boolean b = (Boolean) val;
                llvm.append("  store i1 ").append(b ? "1" : "0").append(", i1* %").append(name).append("\n");
            } else if (llvmType.equals("i32") && val instanceof Integer) {
                llvm.append("  store i32 ").append(val).append(", i32* %").append(name).append("\n");
            } else if (llvmType.equals("double") && val instanceof Double) {
                llvm.append("  store double ").append(val).append(", double* %").append(name).append("\n");
            }
            // strings tratadas via GlobalStringManager
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
