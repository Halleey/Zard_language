package ast.functions;

import ast.ASTNode;
import tokens.Token;
import translate.front.Parser;

import java.util.ArrayList;
import java.util.List;
public class FunctionCallParser {
    private final Parser parser;

    public FunctionCallParser(Parser parser) {
        this.parser = parser;
    }

    public FunctionCallNode parseFunctionCall(String funcStart) {

        StringBuilder funcName = new StringBuilder(funcStart);

        while (parser.current().getValue().equals(".")) {
            parser.advance(); // consome '.'
            String next = parser.current().getValue();
            parser.advance();
            funcName.append(".").append(next);
        }

        parser.eat(Token.TokenType.DELIMITER, "(");

        List<ASTNode> args = new ArrayList<>();

        if (!parser.current().getValue().equals(")")) {
            do {
                args.add(parser.parseExpression());

                if (parser.current().getValue().equals(",")) {
                    parser.advance();
                } else break;

            } while (!parser.current().getValue().equals(")"));
        }

        parser.eat(Token.TokenType.DELIMITER, ")");
        parser.eat(Token.TokenType.DELIMITER, ";");

        return new FunctionCallNode(funcName.toString(), args);
    }
}
