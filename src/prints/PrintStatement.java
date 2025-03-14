package prints;
import expressions.Expression;
import expressions.TypedValue;
import translate.Statement;
import translate.VariableTable;
public class PrintStatement extends Statement {
    public final Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(VariableTable table) {
        TypedValue value = expression.evaluate(table);
        System.out.println(value.getValue()); // Agora imprime corretamente
    }
}

