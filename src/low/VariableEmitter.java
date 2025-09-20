package low;

import java.util.Map;

public class VariableEmitter {
    private final Map<String, String> varTypes;
    private final TempManager temps;

    public VariableEmitter(Map<String, String> varTypes, TempManager temps) {
        this.varTypes = varTypes;
        this.temps = temps;
    }

    public String emitDeclaration(String name, String type, String initializerCode) {
        String llvmType = switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            default -> "i32";
        };

        varTypes.put(name, llvmType);

        StringBuilder llvm = new StringBuilder();
        llvm.append("  %").append(name).append(" = alloca ").append(llvmType).append("\n");

        if (initializerCode != null) {
            String value = initializerCode;
            if (llvmType.equals("double") && !value.contains(".")) value += ".0";
            llvm.append("  store ").append(llvmType).append(" ").append(value)
                    .append(", ").append(llvmType).append("* %").append(name).append("\n");
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