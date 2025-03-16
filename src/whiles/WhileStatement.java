package whiles;

import expressions.Expression;
import ifs.Block;
import variables.Statement;
import variables.VariableTable;

public class WhileStatement extends Statement {
    private final Expression condition;
    private final Block block;

    public WhileStatement(Expression condition, Block block) {
        this.condition = condition;
        this.block = block;
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void execute(VariableTable table) {
        while (condition.evaluate(table).isTruthy()) {  // Avalia a condição
            block.execute(table);  // Executa o bloco do while
        }
    }
}
