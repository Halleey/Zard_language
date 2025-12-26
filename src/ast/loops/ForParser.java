package ast.loops;

import ast.ASTNode;
import tokens.Token;
import translate.front.Parser;
import java.util.List;
import ast.variables.UnaryOpNode;
import ast.variables.VariableNode;


public class ForParser {

    private final Parser parser;
    private static final boolean DEBUG = true;

    public ForParser(Parser parser) {
        this.parser = parser;
    }


    public ASTNode parse() {


        // for
        parser.eat(Token.TokenType.KEYWORD, "for");


        // (
        parser.eat(Token.TokenType.DELIMITER, "(");

        ASTNode init = null;


        if (!parser.current().getValue().equals(";")) {
            init = parser.parseStatement(); // consome ';'
        } else {
            parser.eat(Token.TokenType.DELIMITER, ";");
        }

        ASTNode condition = null;

        if (!parser.current().getValue().equals(";")) {
            condition = parser.parseExpression();
        } else {
        }

        parser.eat(Token.TokenType.DELIMITER, ";");
        ASTNode increment = null;

        if (!parser.current().getValue().equals(")")) {

            // CASO ESPECIAL: i++ ou i--
            if (parser.current().getType() == Token.TokenType.IDENTIFIER
                    && (parser.peek().getValue().equals("++")
                    || parser.peek().getValue().equals("--"))) {

                String name = parser.current().getValue();
                String op = parser.peek().getValue();


                parser.advance(); // consome IDENTIFIER
                parser.advance(); // consome ++ ou --

                increment = new UnaryOpNode(op, new VariableNode(name));

            } else {
                // fallback normal
                increment = parser.parseExpression();
            }

        }

        parser.eat(Token.TokenType.DELIMITER, ")");

        List<ASTNode> body = parser.parseBlock();

        ForNode node = new ForNode(init, condition, increment, body);

        return node;
    }
}
