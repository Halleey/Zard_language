package translate;

import ast.ASTNode;
import ast.functions.FunctionCallNode;

import tokens.Token;
import variables.AssignmentNode;
import variables.UnaryOpNode;
import variables.VariableNode;

import java.util.List;
public class IdentifierParser {
    private final Parser parser;
    private final ListMethodParser listParser;

    public IdentifierParser(Parser parser) {
        this.parser = parser;
        this.listParser = new ListMethodParser(parser);
    }

    public ASTNode parseAsStatement(String name) {
        if (parser.current().getValue().equals(".")) {
            return listParser.parseStatementListMethod(name);
        }

        if (parser.current().getValue().equals("=")) {
            parser.advance();
            ASTNode value = parser.parseExpression();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new AssignmentNode(name, value);
        }

        if (parser.current().getValue().equals("++") || parser.current().getValue().equals("--")) {
            String op = parser.current().getValue();
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new UnaryOpNode(name, op);
        }

        parser.eat(Token.TokenType.DELIMITER, ";");
        return new VariableNode(name);
    }

    public ASTNode parseAsExpression(String name) {
        if (parser.current().getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            return new FunctionCallNode(name, args);
        }

        if (parser.current().getValue().equals(".")) {
            return listParser.parseExpressionListMethod(name);
        }

        return new VariableNode(name);
    }
}
