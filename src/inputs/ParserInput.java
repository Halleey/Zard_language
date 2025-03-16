package inputs;

import interpreter.Parser;
import tokens.Token;

public class ParserInput {
    private final Parser parser;

    public ParserInput(Parser parser) {
        this.parser = parser;
    }

    public InputStatement parseInputStatement() {
        parser.consume(Token.TokenType.KEYWORD);
        parser.consume(Token.TokenType.DELIMITER);
        Token variableToken = parser.consume(Token.TokenType.IDENTIFIER);
        parser.consume(Token.TokenType.DELIMITER);
        parser.consume(Token.TokenType.DELIMITER);

        return new InputStatement(variableToken.getValue());
    }
}
