package low.variables;

import low.main.GlobalStringManager;
import low.TempManager;
import variables.LiteralNode;

public class LiteralEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public LiteralEmitter(TempManager temps, GlobalStringManager globalStrings) {
        this.temps = temps;
        this.globalStrings = globalStrings;
    }

    public String emit(LiteralNode node) {
        String type = switch (node.value.getType()) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string" -> "i8*";
            default -> throw new RuntimeException("Tipo literal desconhecido: " + node.value.getType());
        };

        Object val = node.value.getValue();
        if (type.equals("double") && val instanceof Integer) val = ((Integer) val).doubleValue();

        String tmp = temps.newTemp();

        if (type.equals("i32") || type.equals("double") || type.equals("i1")) {
            String llvmVal = switch (type) {
                case "i32" -> val.toString();
                case "double" -> val.toString();
                case "i1" -> ((Boolean) val ? "1" : "0");
                default -> throw new RuntimeException("Tipo inesperado: " + type);
            };
            return "  " + tmp + " = add " + type + " 0, " + llvmVal + "\n;;VAL:" + tmp + ";;TYPE:" + type + "\n";
        }

        if (type.equals("i8*")) {
            String strName = globalStrings.getOrCreateString((String) val);
            int len = ((String) val).length() + 2;
            String llvm = "  " + tmp + " = getelementptr ([" + len + " x i8], [" + len + " x i8]* " + strName + ", i32 0, i32 0)\n";
            llvm += ";;VAL:" + tmp + ";;TYPE:" + type + "\n";
            return llvm;
        }

        throw new RuntimeException("LiteralNode não suportado: " + type);
    }
}