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

    public IdentifierParser(Parser parser) {
        this.parser = parser;
    }
    public ASTNode parseAsStatement(String name) {
        String tokenVal = parser.current().getValue();

        // atribuição
        if ("=".equals(tokenVal)) {
            parser.advance();
            ASTNode value = parser.parseExpression();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new AssignmentNode(name, value);
        }

        if ("++".equals(tokenVal) || "--".equals(tokenVal)) {
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new UnaryOpNode(name, tokenVal);
        }

        return new VariableNode(name);
    }


    public ASTNode parseAsExpression(String name) {
        if (parser.current().getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            return new FunctionCallNode(name, args);
        }
        return new VariableNode(name);
    }
}
