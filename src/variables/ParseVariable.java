package variables;

import expressions.Expression;
import interpreter.Parser;
import tokens.Token;

public class ParseVariable {

    private final Parser parser;

    public ParseVariable(Parser parser) {
        this.parser = parser;

    }
    public Statement parseVariableAssignment() {
        Token nameToken = parser.consume(Token.TokenType.IDENTIFIER);

        if (parser.match(Token.TokenType.OPERATOR)) {
            Token operatorToken = parser.tokens.get(parser.pos);
            String op = operatorToken.getValue();

            if (op.equals("++") || op.equals("--")) {
                parser.consume(Token.TokenType.OPERATOR);
                parser.consume(Token.TokenType.DELIMITER);
                return new IncrementDecrementStatement(nameToken.getValue(), op);
            }

            parser.consume(Token.TokenType.OPERATOR);
            Expression value = parser.parseExpression();
            parser.consume(Token.TokenType.DELIMITER);
            return new VariableAssignment(nameToken.getValue(), value);
        }

        throw new RuntimeException("Erro de sintaxe: esperado operador após '" + nameToken.getValue() + "'");
    }


}
