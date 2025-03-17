package returns;

import expressions.Expression;
import variables.Statement;
import variables.VariableTable;

public class ReturnStatement extends Statement {
    private final Expression value; // O valor retornado, se houver

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ReturnStatement{" + (value != null ? value.toString() : "void") + "}";
    }

    @Override
    public void execute(VariableTable table) {
        throw new ReturnException(value); // Interrompe a execução do bloco
    }

}
