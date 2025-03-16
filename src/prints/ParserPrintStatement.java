package prints;

import expressions.Expression;
import interpreter.Parser;
import tokens.Token;

public class ParserPrintStatement {

    private final Parser parser;

    public ParserPrintStatement(Parser parser) {
        this.parser = parser;
    }

    public PrintStatement parsePrintStatement() {
        parser.consume(Token.TokenType.KEYWORD);
        parser.consume(Token.TokenType.DELIMITER);
        Expression expression = parser.parseExpression();
        parser.consume(Token.TokenType.DELIMITER);
        parser.consume(Token.TokenType.DELIMITER);
        return new PrintStatement(expression);
    }

}
