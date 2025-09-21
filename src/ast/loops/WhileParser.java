package ast.loops;

import ast.ASTNode;
import tokens.Token;
import translate.Parser;

import java.util.List;

public class WhileParser {
    private final Parser parser;

    public WhileParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parse() {
        // Consome 'while'
        parser.eat(Token.TokenType.KEYWORD, "while");

        // Espera '('
        parser.eat(Token.TokenType.DELIMITER, "(");

        // Condição
        ASTNode condition = parser.parseExpression();

        // Fecha ')'
        parser.eat(Token.TokenType.DELIMITER, ")");

        // Corpo do while
        List<ASTNode> body = parser.parseBlock();
        
        return new WhileNode(condition, body);
    }
}
