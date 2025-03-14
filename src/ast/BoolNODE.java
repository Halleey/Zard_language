package ast;

import expressions.Expression;
import expressions.TypedValue;
import tokens.Token;
import variables.VariableTable;

public class BoolNODE extends Expression {
    private final Token token;

    public BoolNODE(Token token) {
        this.token = token;
    }

    @Override
    public TypedValue evaluate(VariableTable table) {
        boolean boolValue = Boolean.parseBoolean(token.getValue()); // Converte "true" ou "false" para um boolean real
        return new TypedValue(boolValue, "boolean");
    }
}
