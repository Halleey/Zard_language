package returns;

import expressions.Expression;

public class ReturnException extends RuntimeException {
    private final Expression value; // Valor retornado

    public ReturnException(Expression value) {
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }
}
