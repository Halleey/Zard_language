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
        parser.advance(); // consome 'function'

        // Nome da função
        String funcName = parser.current().getValue();
        parser.advance();

        // Parâmetros entre '(' e ')'
        parser.eat(Token.TokenType.DELIMITER, "(");
        List<String> paramNames = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        if (!parser.current().getValue().equals(")")) {
            do {
                // Pega o tipo do parâmetro
                String type = parser.current().getValue();
                parser.advance();

                // Pega o nome do parâmetro
                String name = parser.current().getValue();
                parser.advance();

                paramNames.add(name);
                paramTypes.add(type);

                if (parser.current().getValue().equals(",")) {
                    parser.advance();
                } else {
                    break;
                }
            } while (!parser.current().getValue().equals(")"));
        }
        parser.eat(Token.TokenType.DELIMITER, ")");

        // Cria um contexto local para a função
        parser.pushContext();

        // Declara os parâmetros no contexto
        for (int i = 0; i < paramNames.size(); i++) {
            parser.declareVariable(paramNames.get(i), paramTypes.get(i));
        }

        // Corpo da função
        List<ASTNode> body = parser.parseBlock();

        // Remove o contexto local
        parser.popContext();

        return new FunctionNode(funcName, paramNames, body);
    }
}
