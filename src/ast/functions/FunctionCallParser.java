package ast.functions;

import ast.ASTNode;
import tokens.Token;
import translate.Parser;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallParser {

    private final Parser parser;

    public FunctionCallParser(Parser parser) {
        this.parser = parser;
    }

    public FunctionCallNode parseFunctionCall(String funcName) {
        parser.eat(Token.TokenType.DELIMITER, "("); // consome '('

        List<ASTNode> args = new ArrayList<>();
        if (!parser.current().getValue().equals(")")) {
            do {
                ASTNode expr = parser.parseExpression();
                args.add(expr);

                if (parser.current().getValue().equals(",")) {
                    parser.advance();
                } else {
                    break;
                }
            } while (!parser.current().getValue().equals(")"));
        }

        parser.eat(Token.TokenType.DELIMITER, ")");
        parser.eat(Token.TokenType.DELIMITER, ";");

        return new FunctionCallNode(funcName, args);
    }

}
