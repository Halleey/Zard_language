package returns;

import expressions.Expression;

public class ReturnException extends RuntimeException {
    private final Expression value; // Pode ser null, se não houver valor

    public ReturnException(Expression value) {
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }
}

