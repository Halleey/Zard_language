package returns;

import expressions.Expression;
import variables.Statement;
import variables.VariableTable;

public class ReturnStatement extends Statement {
    private final Expression value; // Opcional, para suportar `return <valor>;`

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    public ReturnStatement() {
        this.value = null;
    }

    @Override
    public void execute(VariableTable table) {
        throw new ReturnException(value); // Agora passa corretamente o valor do retorno
    }
}
