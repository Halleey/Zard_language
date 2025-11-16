package ast.prints;

import ast.ASTNode;
import tokens.Token;
import translate.front.Parser;

public class PrintParser {
    private final Parser parser;

    public PrintParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parsePrint() {
        parser.advance();
        parser.eat(Token.TokenType.DELIMITER, "(");

        ASTNode expr = parser.parseExpression();

        parser.eat(Token.TokenType.DELIMITER, ")");
        parser.eat(Token.TokenType.DELIMITER, ";");

        return new PrintNode(expr);
    }
}
