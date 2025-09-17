package translate;

import ast.ASTNode;
import ast.functions.FunctionCallNode;

import expressions.TypedValue;
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
        String type = parser.getVariableType(name);
        String tokenVal = parser.current().getValue();

        // prioridade: se for lista e o token atual é '.', parsear método de lista
        if ("list".equals(type) && ".".equals(tokenVal)) {
            return listParser.parseStatementListMethod(name); // já consome '.' + método + ';'
        }

        // atribuição
        if ("=".equals(tokenVal)) {
            parser.advance();
            ASTNode value = parser.parseExpression();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new AssignmentNode(name, value);
        }

        // unário ++ / --
        if ("++".equals(tokenVal) || "--".equals(tokenVal)) {
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new UnaryOpNode(name, tokenVal);
        }

        // variável sozinha → statement inútil
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
