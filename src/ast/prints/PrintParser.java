package ast.prints;

import ast.ASTNode;
import tokens.Token;
import translate.front.Parser;
public class PrintParser {
    private final Parser parser;
    private final boolean newline;

    public PrintParser(Parser parser, boolean newline) {
        this.parser = parser;
        this.newline = newline;
    }

    public ASTNode parsePrint() {
        parser.advance(); // print / println
        parser.eat(Token.TokenType.DELIMITER, "(");

        ASTNode expr = parser.parseExpression();

        parser.eat(Token.TokenType.DELIMITER, ")");
        parser.eat(Token.TokenType.DELIMITER, ";");

        return new PrintNode(expr, newline);
    }
}
