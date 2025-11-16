package ast.home;

import ast.ASTNode;
import tokens.Token;
import translate.front.Parser;

import java.util.ArrayList;
import java.util.List;

public class MainParser {

    private final Parser parser;

    public MainParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseMain() {
        // Consome 'main'
        parser.eat(Token.TokenType.KEYWORD, "main");

        // Espera '{'
        parser.eat(Token.TokenType.DELIMITER, "{");

        // Lê o corpo
        List<ASTNode> body = new ArrayList<>();
        while (!parser.current().getValue().equals("}")) {
            body.add(parser.parseStatement());
        }

        // Fecha '}'
        parser.eat(Token.TokenType.DELIMITER, "}");

        return new MainAST(body);
    }


}
