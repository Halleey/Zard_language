package low.variables;

import ast.expressions.TypedValue;
import ast.variables.LiteralNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.*;

public class LiteralEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public LiteralEmitter(TempManager temps, GlobalStringManager globalStrings) {
        this.temps = temps;
        this.globalStrings = globalStrings;
    }

    public LLVMValue emit(LiteralNode node) {
        TypedValue value = node.value;
        Type type = value.type();
        String temp = temps.newTemp();
        StringBuilder llvm = new StringBuilder();
        LLVMTYPES llvmType;

        if (type.equals(PrimitiveTypes.INT)) {
            llvm.append("  ").append(temp).append(" = add i32 0, ").append(value.value()).append("\n");
            llvmType = new LLVMInt();

        } else if (type.equals(PrimitiveTypes.DOUBLE)) {
            llvm.append("  ").append(temp).append(" = fadd double 0.0, ").append(value.value()).append("\n");
            llvmType = new LLVMDouble();

        } else if (type.equals(PrimitiveTypes.FLOAT)) {
            String val = value.value().toString();
            if (!val.endsWith("f") && !val.endsWith("F")) val += "f";
            llvm.append("  ").append(temp).append(" = fadd float 0.0, ").append(val).append("\n");
            llvmType = new LLVMFloat();

        } else if (type.equals(PrimitiveTypes.BOOL)) {
            boolean b = (Boolean) value.value();
            llvm.append("  ").append(temp).append(" = add i1 0, ").append(b ? 1 : 0).append("\n");
            llvmType = new LLVMBool();

        } else if (type.equals(PrimitiveTypes.CHAR)) {
            char c;
            Object raw = value.value();
            if (raw instanceof Character) c = (Character) raw;
            else if (raw instanceof String s && s.length() == 1) c = s.charAt(0);
            else throw new RuntimeException("Invalid char literal: " + raw);
            int ascii = c;
            llvm.append("  ").append(temp).append(" = add i8 0, ").append(ascii).append("\n");
            llvmType = new LLVMChar();

        } else if (type.equals(PrimitiveTypes.STRING)) {
            String literal = (String) value.value();
            String strName = globalStrings.getOrCreateString(literal);
            temp = temps.newTemp();
            llvm.append("  ").append(temp)
                    .append(" = call %String* @createString(i8* ").append(strName).append(")\n");
            llvmType = new LLVMString();

        } else if (type.equals(PrimitiveTypes.VOID)) {
            llvmType = new LLVMVoid();
            temp = "void";

        } else {
            throw new RuntimeException("Literal type not supported: " + type);
        }

        return new LLVMValue(llvmType, temp, llvm.toString());
    }
}