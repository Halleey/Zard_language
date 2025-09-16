package ast.exceptions;

import expressions.TypedValue;

public class ReturnValue extends RuntimeException {
    public final TypedValue value;

    public ReturnValue(TypedValue value) {
        this.value = value;
    }
}
