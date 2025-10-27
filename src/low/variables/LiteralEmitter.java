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

        switch (value.type()) {
            case "int" -> {
                llvm.append("  ").append(temp).append(" = add i32 0, ").append(value.value()).append("\n");
                llvm.append(";;VAL:").append(temp).append(";;TYPE:i32\n");
            }
            case "double" -> {
                // Gera literal double diretamente sem somar 0
                llvm.append("  ").append(temp).append(" = fadd double 0.0, ")
                        .append(value.value()).append("\n");
                llvm.append(";;VAL:").append(temp).append(";;TYPE:double\n");
            }
            case "boolean" -> {
                boolean b = (Boolean) value.value();
                llvm.append("  ").append(temp).append(" = add i1 0, ").append(b ? 1 : 0).append("\n");
                llvm.append(";;VAL:").append(temp).append(";;TYPE:i1\n");
            }
            case "char" -> {
                String str = (String) value.value();
                if (str.length() != 1) {
                    throw new RuntimeException("Invalid char literal: " + str);
                }
                int ascii = str.charAt(0);
                llvm.append("  ").append(temp).append(" = add i8 0, ").append(ascii).append("\n")
                        .append(";;VAL:").append(temp).append(";;TYPE:i8\n");
            }
            case "string" -> {
                String literal = (String) value.value();
                String strName = globalStrings.getOrCreateString(literal); // registrar @str
                String tmp = temps.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = call %String* @createString(i8* ").append(strName).append(")\n")
                        .append(";;VAL:").append(tmp).append(";;TYPE:%String*\n");
            }
            default -> throw new RuntimeException("Literal type not supported: " + value.type());
        }

        return llvm.toString();
    }
}
