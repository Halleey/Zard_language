package expressions;

public class ReturnException extends RuntimeException {
    public final Object value;

    public ReturnException(Object value) {
        super(null, null, false, false);  // Desativa o rastreamento do stack trace para performance
        this.value = value;
    }
}

