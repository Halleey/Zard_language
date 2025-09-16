package ast.inputs;

import ast.ASTNode;
import tokens.Token;
import translate.Parser;

public class InputParser  {
    private final Parser parser;

    public InputParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parse() {
        // Espera a keyword "input"
        parser.eat(Token.TokenType.KEYWORD, "input");

        // Espera '('
        parser.eat(Token.TokenType.DELIMITER, "(");

        String prompt = "";

        if (parser.current().getType() == Token.TokenType.STRING) {
            prompt = parser.current().getValue();
            parser.advance();
        }

        // Fecha ')'
        parser.eat(Token.TokenType.DELIMITER, ")");

        return new InputNode(prompt);
    }
}

