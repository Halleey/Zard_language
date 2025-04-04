package expressions;

import tokens.Token;
import variables.VariableTable;


public class LiteralExpression extends Expression {
    public final Token token;

    public LiteralExpression(Token token) {
        this.token = token;
    }

    @Override
    public TypedValue evaluate(VariableTable table) {
        String value = token.getValue();
        Token.TokenType type = token.getType();

        if (type == Token.TokenType.NUMBER) {
            if (value.contains(".")) {
                return new TypedValue(Double.parseDouble(value), "double");
            }
            return new TypedValue(Integer.parseInt(value), "int");
        } else if (type == Token.TokenType.STRING) {
            return new TypedValue(value, "string");
        } else if (type == Token.TokenType.BOOLEAN) {  // dando suuporte a booleanos
            return new TypedValue(Boolean.parseBoolean(value), "bool");
        }

        throw new RuntimeException("Tipo de literal não suportado: " + type);
    }

    @Override
    public String toString() {
        String value = token.getValue();
        Token.TokenType type = token.getType();

        if (type == Token.TokenType.STRING) {
            return "\"" + value + "\""; // Aspas em strings
        }
        return value; // Deixa números e booleanos como estão
    }

}



