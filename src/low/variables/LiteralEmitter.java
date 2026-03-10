package low.variables;

import ast.expressions.TypedValue;
import ast.variables.LiteralNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.TempManager;
import low.main.GlobalStringManager;

public class LiteralEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public LiteralEmitter(TempManager temps, GlobalStringManager globalStrings) {
        this.temps = temps;
        this.globalStrings = globalStrings;
    }

    public String emit(LiteralNode node) {
        TypedValue value = node.value;
        Type type = value.type();
        String temp = temps.newTemp();
        StringBuilder llvm = new StringBuilder();

        if (type.equals(PrimitiveTypes.INT)) {
            llvm.append("  ").append(temp)
                    .append(" = add i32 0, ").append(value.value()).append("\n")
                    .append(";;VAL:").append(temp).append(";;TYPE:i32\n");

        } else if (type.equals(PrimitiveTypes.DOUBLE)) {
            llvm.append("  ").append(temp)
                    .append(" = fadd double 0.0, ").append(value.value()).append("\n")
                    .append(";;VAL:").append(temp).append(";;TYPE:double\n");

        } else if (type.equals(PrimitiveTypes.FLOAT)) {
            String val = value.value().toString();
            if (!val.endsWith("f") && !val.endsWith("F")) val += "f";
            llvm.append("  ").append(temp)
                    .append(" = fadd float 0.0, ").append(val).append("\n")
                    .append(";;VAL:").append(temp).append(";;TYPE:float\n");

        } else if (type.equals(PrimitiveTypes.BOOL)) {
            boolean b = (Boolean) value.value();
            llvm.append("  ").append(temp)
                    .append(" = add i1 0, ").append(b ? 1 : 0).append("\n")
                    .append(";;VAL:").append(temp).append(";;TYPE:i1\n");

        } else if (type.equals(PrimitiveTypes.CHAR)) {
            Object raw = value.value();
            char c;
            if (raw instanceof Character) {
                c = (Character) raw;
            } else if (raw instanceof String s && s.length() == 1) {
                c = s.charAt(0);
            } else {
                throw new RuntimeException("Invalid char literal: " + raw);
            }
            int ascii = c;
            llvm.append("  ").append(temp)
                    .append(" = add i8 0, ").append(ascii).append("\n")
                    .append(";;VAL:").append(temp).append(";;TYPE:i8\n");

        } else if (type.equals(PrimitiveTypes.STRING)) {
            // Sempre criar %String* heap
            String literal = (String) value.value();
            String strName = globalStrings.getOrCreateString(literal);
            String tmp = temps.newTemp();
            llvm.append("  ").append(tmp)
                    .append(" = call %String* @createString(i8* ").append(strName).append(")\n")
                    .append(";;VAL:").append(tmp).append(";;TYPE:%String*\n");

        } else if (type.equals(PrimitiveTypes.VOID)) {
            llvm.append(";;VAL:void;;TYPE:void\n");

        } else {
            throw new RuntimeException("Literal type not supported: " + type);
        }

        return llvm.toString();
    }
}