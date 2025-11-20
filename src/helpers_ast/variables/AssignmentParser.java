package helpers_ast.variables;

import ast.ASTNode;
import ast.variables.AssignmentNode;
import tokens.Token;
import translate.front.Parser;

public class AssignmentParser {
    private final Parser parser;

    public AssignmentParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parse (String nome) {
        parser.advance();
        ASTNode node = parser.parseExpression();
        parser.eat(Token.TokenType.DELIMITER, ";");
        return new AssignmentNode(nome, node);
    }

}