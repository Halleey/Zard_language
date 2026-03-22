package low.prints;


import ast.ASTNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMString;
import low.module.builders.primitives.LLVMVoid;

public class StringVariablePrintHandler implements PrintHandler {

    private final TempManager temps;

    public StringVariablePrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        if (node instanceof VariableNode varNode) {
            TypeInfos info = visitor.getVarType(varNode.getName());
            return info != null && info.getLLVMType() instanceof LLVMString;
        }
        return false;
    }

    @Override
    public LLVMValue emit(ASTNode node, LLVisitorMain visitor, boolean newline) {

        String varName = ((VariableNode) node).getName();

        LLVMValue loaded = visitor.varEmitter.emitLoad(varName);

        String fn = newline ? "@printString" : "@printString_noNL";

        StringBuilder llvm = new StringBuilder();
        llvm.append(loaded.getCode());

        llvm.append("  call void ")
                .append(fn)
                .append("(%String* ")
                .append(loaded.getName())
                .append(")\n");

        return new LLVMValue(new LLVMVoid(), "void", llvm.toString());
    }
}