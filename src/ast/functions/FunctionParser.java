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

        String funcName = parser.current().getValue();
        parser.advance();

        parser.eat(Token.TokenType.DELIMITER, "(");
        List<String> paramNames = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        if (!parser.current().getValue().equals(")")) {
            do {
                // pega tipo e nome
                String type = parser.current().getValue();
                parser.advance();
                String name = parser.current().getValue();
                parser.advance();

                paramNames.add(name);
                paramTypes.add(type);

                if (parser.current().getValue().equals(",")) parser.advance();
                else break;
            } while (!parser.current().getValue().equals(")"));
        }
        parser.eat(Token.TokenType.DELIMITER, ")");

        parser.pushContext();

        // declara variáveis com tipo
        for (int i = 0; i < paramNames.size(); i++) {
            parser.declareVariable(paramNames.get(i), paramTypes.get(i));
        }

        List<ASTNode> body = parser.parseBlock();
        parser.popContext();

        return new FunctionNode(funcName, paramNames, paramTypes, body); // passa os tipos
    }

}
