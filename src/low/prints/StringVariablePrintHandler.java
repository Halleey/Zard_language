package low.prints;


import ast.ASTNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
public class StringVariablePrintHandler implements PrintHandler {
    private final TempManager temps;

    public StringVariablePrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        if (node instanceof VariableNode varNode) {
            TypeInfos info = visitor.getVarType(varNode.getName());
            if (info == null) return false;
            String llvmType = info.getLLVMType();
            return "%String".equals(llvmType) || "%String*".equals(llvmType);
        }
        return false;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor, boolean newline) {
        String varName = ((VariableNode) node).getName();
        String tmpLoad = temps.newTemp();
        String fn = newline ? "@printString" : "@printString_noNL";

        return "  " + tmpLoad + " = load %String*, %String** %" + varName + "\n" +
                "  call void " + fn + "(%String* " + tmpLoad + ")\n";
    }
}
