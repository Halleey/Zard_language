package helpers_ast.variables;

import ast.ASTNode;
import ast.variables.UnaryOpNode;
import ast.variables.VariableNode;
import tokens.Token;
import translate.front.Parser;

public class UnaryParser {

    private final Parser parser;

    public UnaryParser(Parser parser) {
        this.parser = parser;

    }

    public ASTNode parser(String name, String op) {
        parser.advance();
        parser.eat(Token.TokenType.DELIMITER, ";");
        return new UnaryOpNode(op, new VariableNode(name));
    }

}