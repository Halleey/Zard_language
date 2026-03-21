package low.exceptions;

import ast.exceptions.ReturnNode;
import ast.variables.LiteralNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMChar;
import low.module.builders.primitives.LLVMString;
public class ReturnEmitter {

    private final LLVisitorMain visitor;
    private final TempManager temps;

    public ReturnEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
        this.temps = temps;
    }

    public LLVMValue emit(ReturnNode node) {

        StringBuilder llvm = new StringBuilder();

        // void
        if (node.expr == null) {
            llvm.append("  ret void\n");
            return new LLVMValue(null, "", llvm.toString());
        }

        LLVMValue val = node.expr.accept(visitor);
        llvm.append(val.getCode());

        LLVMTYPES type = val.getType();
        String temp = val.getName();

        // ===== CASO STRING JÁ PRONTA (%String*)
        if (type instanceof LLVMString) {
            llvm.append("  ret %String* ").append(temp).append("\n");
            return new LLVMValue(type, temp, llvm.toString());
        }

        //  CASO i8* → wrap em %String
        if (type instanceof LLVMPointer ptr && ptr.pointee() instanceof LLVMChar) {

            String sAlloca = temps.newTemp();
            llvm.append("  ").append(sAlloca).append(" = alloca %String\n");

            String fld0 = temps.newTemp();
            llvm.append("  ").append(fld0)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(sAlloca).append(", i32 0, i32 0\n");

            llvm.append("  store i8* ")
                    .append(temp)
                    .append(", i8** ")
                    .append(fld0)
                    .append("\n");

            String fld1 = temps.newTemp();
            llvm.append("  ").append(fld1)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(sAlloca).append(", i32 0, i32 1\n");

            llvm.append("  store i64 0, i64* ")
                    .append(fld1)
                    .append("\n");

            llvm.append("  ret %String* ").append(sAlloca).append("\n");

            return new LLVMValue(new LLVMString(), sAlloca, llvm.toString());
        }

        // ===== CASO GERAL
        llvm.append("  ret ")
                .append(type)
                .append(" ")
                .append(temp)
                .append("\n");

        return new LLVMValue(type, temp, llvm.toString());
    }
}