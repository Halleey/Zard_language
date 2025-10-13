package low.prints;


import ast.ASTNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.module.LLVisitorMain;

public class StringVariablePrintHandler implements PrintHandler {
    private final TempManager temps;

    public StringVariablePrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        if (node instanceof VariableNode varNode) {
            String type = visitor.getVarType(varNode.getName());
            return "%String".equals(type) || "%String*".equals(type);
        }
        return false;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor) {
        String varName = ((VariableNode) node).getName();
        String tmpLoad = temps.newTemp();
        return "  " + tmpLoad + " = load %String*, %String** %" + varName + "\n" +
                "  call void @printString(%String* " + tmpLoad + ")\n";
    }
}
