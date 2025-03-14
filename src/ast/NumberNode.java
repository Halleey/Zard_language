package ast;

import expressions.Expression;
import expressions.TypedValue;
import tokens.Token;
import translate.VariableTable;

public class NumberNode extends Expression {
    private final Token token;

    public NumberNode(Token token) {
        this.token = token;
    }

    @Override
    public TypedValue evaluate(VariableTable table) {
        String value = token.getValue();
        if (value.contains(".")) {
            return new TypedValue(Double.parseDouble(value), "double"); // Se houver ponto decimal, é double
        }
        return new TypedValue(Integer.parseInt(value), "int"); // Caso contrário, é int
    }
}


