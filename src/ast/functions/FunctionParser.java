package ast.functions;

import ast.ASTNode;
import tokens.Token;
import translate.Parser;

import java.util.ArrayList;
import java.util.List;

public class FunctionParser {

    private final Parser parser;

    public FunctionParser(Parser parser) {
        this.parser = parser;
    }

    public FunctionNode parseFunction() {
        parser.advance(); // consome 'func'

        // Nome da função
        String funcName = parser.current().getValue();
        parser.advance();

        // Parâmetros entre '(' e ')'
        parser.eat(Token.TokenType.DELIMITER, "(");
        List<String> params = new ArrayList<>();
        if (!parser.current().getValue().equals(")")) {
            do {
                // Pega o tipo do parâmetro
                String type = parser.current().getValue();
                parser.advance();

                // Pega o nome do parâmetro
                String name = parser.current().getValue();
                parser.advance();

                // Guarda "tipo nome" na lista de parâmetros
                params.add(type + " " + name);

                if (parser.current().getValue().equals(",")) {
                    parser.advance();
                } else {
                    break;
                }
            } while (!parser.current().getValue().equals(")"));
        }
        parser.eat(Token.TokenType.DELIMITER, ")");

        // Corpo da função
        List<ASTNode> body = parser.parseBlock();

        return new FunctionNode(funcName, params, body);
    }

}
