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

        // Lê tipo de retorno (opcional)
        String returnType = "void";
        if (parser.current().getType() == Token.TokenType.KEYWORD || parser.current().getType() == Token.TokenType.IDENTIFIER) {
            returnType = parser.current().getValue();
            parser.advance();
        }

        // Agora lê o nome da função
        String funcName = parser.current().getValue();
        parser.advance();

        parser.eat(Token.TokenType.DELIMITER, "(");
        List<String> paramNames = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        if (!parser.current().getValue().equals(")")) {
            do {
                String type = parser.current().getValue(); // tipo do param
                parser.advance();
                String name = parser.current().getValue(); // nome do param
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

        parser.pushContext();
        for (int i = 0; i < paramNames.size(); i++) {
            parser.declareVariable(paramNames.get(i), paramTypes.get(i));
        }

        List<ASTNode> body = parser.parseBlock();
        parser.popContext();

        return new FunctionNode(funcName, paramNames, paramTypes, body, returnType);
    }

}
