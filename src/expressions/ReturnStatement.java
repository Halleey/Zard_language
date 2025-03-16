package expressions;

import variables.Statement;
import variables.VariableTable;

public class ReturnStatement extends Statement {
    private final Expression returnValue;

    public ReturnStatement(Expression returnValue) {
        this.returnValue = returnValue;
    }

    public Expression getReturnValue() {
        return returnValue;
    }

    @Override
    public void execute(VariableTable table) {
        throw new ReturnException(returnValue != null ? returnValue.evaluate(table) : null);
    }
}


