package low.variables;

import ast.expressions.TypedValue;
import low.main.GlobalStringManager;
import low.TempManager;
import ast.variables.LiteralNode;


public class LiteralEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public LiteralEmitter(TempManager temps, GlobalStringManager globalStrings) {
        this.temps = temps;
        this.globalStrings = globalStrings;
    }

    public String emit(LiteralNode node) {
        TypedValue value = node.value;
        String temp = temps.newTemp();
        StringBuilder llvm = new StringBuilder();

        switch (value.getType()) {
            case "int" -> {
                llvm.append("  ").append(temp).append(" = add i32 0, ").append(value.getValue()).append("\n");
                llvm.append(";;VAL:").append(temp).append(";;TYPE:i32\n");
            }
            case "double" -> {
                // Gera literal double diretamente sem somar 0
                llvm.append("  ").append(temp).append(" = fadd double 0.0, ")
                        .append(value.getValue()).append("\n");
                llvm.append(";;VAL:").append(temp).append(";;TYPE:double\n");
            }
            case "boolean" -> {
                boolean b = (Boolean) value.getValue();
                llvm.append("  ").append(temp).append(" = add i1 0, ").append(b ? 1 : 0).append("\n");
                llvm.append(";;VAL:").append(temp).append(";;TYPE:i1\n");
            }
            case "string" -> {
                String strName = globalStrings.getOrCreateString((String) value.getValue());
                int len = ((String) value.getValue()).length() + 1;
                llvm.append("  ").append(temp)
                        .append(" = bitcast [").append(len).append(" x i8]* ")
                        .append(strName).append(" to i8*\n");
                llvm.append(";;VAL:").append(temp).append(";;TYPE:i8*\n");
            }

            default -> throw new RuntimeException("Literal type not supported: " + value.getType());
        }

        return llvm.toString();
    }
}
