package low;

import expressions.TypedValue;

import java.util.Map;
public class VariableEmitter {
    private final Map<String, String> varTypes;
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public VariableEmitter(Map<String, String> varTypes, TempManager temps, GlobalStringManager globalStrings) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
    }

    // Declaração de variáveis
    public String emitDeclaration(String name, TypedValue value, String astType) {
        String type = value != null ? value.getType() : astType;
        Object val = value != null ? value.getValue() : null;

        String llvmType = switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string" -> "i8*";
            default -> "i32";
        };

        varTypes.put(name, llvmType);
        StringBuilder llvm = new StringBuilder();

        // Aloca variável
        llvm.append("  %").append(name).append(" = alloca ").append(llvmType).append("\n");

        // Inicializa se houver valor
        if (val != null) {
            if (llvmType.equals("double") && val instanceof Integer) val = ((Integer) val).doubleValue();

            switch (llvmType) {
                case "i1" -> llvm.append("  store i1 ").append((Boolean) val ? "1" : "0").append(", i1* %").append(name).append("\n");
                case "i32" -> llvm.append("  store i32 ").append(val).append(", i32* %").append(name).append("\n");
                case "double" -> llvm.append("  store double ").append(val).append(", double* %").append(name).append("\n");
                case "i8*" -> {
                    String strName = globalStrings.getOrCreateString((String) val);
                    int len = ((String) val).length() + 2; // \n + \0
                    llvm.append("  store i8* getelementptr ([").append(len).append(" x i8], [").append(len).append(" x i8]* ")
                            .append(strName).append(", i32 0, i32 0), i8** %").append(name).append("\n");
                }

            }
        }

        return llvm.toString();
    }

    // Carrega variável
    public String emitLoad(String name) {
        String type = varTypes.getOrDefault(name, "i32");
        String tmp = temps.newTemp();
        if (type.equals("i8*")) {
            return "  " + tmp + " = load i8*, i8** %" + name + "\n" + ";;VAL:" + tmp + ";;TYPE:" + type;
        } else {
            return "  " + tmp + " = load " + type + ", " + type + "* %" + name + "\n" + ";;VAL:" + tmp + ";;TYPE:" + type;
        }
    }
}
