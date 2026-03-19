package low.prints;

import ast.ASTNode;
import ast.variables.LiteralNode;
import context.statics.symbols.PrimitiveTypes;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMString;

public class StringLiteralPrintHandler implements PrintHandler {

    private final GlobalStringManager stringManager;
    private final TempManager temps;

    public StringLiteralPrintHandler(GlobalStringManager stringManager, TempManager temps) {
        this.stringManager = stringManager;
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        return node instanceof LiteralNode lit
                && lit.value.type().equals(PrimitiveTypes.STRING);
    }

    @Override
    public LLVMValue emit(ASTNode node, LLVisitorMain visitor, boolean newline) {

        LiteralNode lit = (LiteralNode) node;
        String value = (String) lit.value.value();

        String strName = stringManager.getOrCreateString(value);
        String tmp = temps.newTemp();

        StringBuilder llvm = new StringBuilder();

        llvm.append("  ").append(tmp)
                .append(" = call %String* @createString(i8* ")
                .append(strName).append(")\n");

        String fn = newline ? "@printString" : "@printString_noNL";

        llvm.append("  call void ")
                .append(fn)
                .append("(%String* ")
                .append(tmp)
                .append(")\n");

        return new LLVMValue(new LLVMString(), tmp, llvm.toString());
    }
}