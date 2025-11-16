package ast.structs;

import ast.functions.FunctionNode;
import ast.functions.FunctionParser;
import tokens.Token;
import translate.front.Parser;

import java.util.ArrayList;
import java.util.List;

public class ImplementsParser {
    private final Parser parser;

    public ImplementsParser(Parser parser) {
        this.parser = parser;
    }

    public ImplNode implNode() {
        parser.advance(); // consome 'impl'

        String structName = parser.current().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);
        parser.eat(Token.TokenType.DELIMITER, "{");

        List<FunctionNode> methods = new ArrayList<>();

        while (!parser.current().getValue().equals("}")) {

            FunctionParser functionParser = new FunctionParser(parser, structName);

            FunctionNode fn = functionParser.parseFunction();

            methods.add(fn);
        }

        parser.eat(Token.TokenType.DELIMITER, "}");
        return new ImplNode(structName, methods);
    }
}
