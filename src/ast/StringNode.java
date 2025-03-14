package ast;

import expressions.Expression;
import expressions.TypedValue;
import tokens.Token;
import translate.VariableTable;

public class StringNode extends Expression {
    private final Token token;

    public StringNode(Token token) {
        this.token = token;
    }

    @Override
    public TypedValue evaluate(VariableTable table) {
        return new TypedValue(token.getValue(), "string");
    }
}




