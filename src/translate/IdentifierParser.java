package translate;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionReferenceNode;
import ast.maps.MapMethodParser;
import expressions.TypedValue;
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
        // Se houver chamada de função ou namespace
        if (parser.current().getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            return new FunctionCallNode(name, args); // chamada normal de função
        }


        if (parser.current().getValue().equals(".")) {
            String varType = parser.getVariableType(name);

            if ("list".equals(varType)) {
                ListMethodParser listParser = new ListMethodParser(parser);
                return listParser.parseExpressionListMethod(name);
            } else if ("map".equals(varType)) {
                MapMethodParser mapParser = new MapMethodParser(parser);
                return mapParser.parseExpressionMapMethod(name);
            } else {
                // Continua suporte a namespace/função
                parser.advance();
                String memberName = parser.current().getValue();
                parser.advance();

                String fullName = name + "." + memberName;

                if (parser.current().getValue().equals("(")) {
                    List<ASTNode> args = parser.parseArguments();
                    return new FunctionCallNode(fullName, args);
                }

                return new FunctionReferenceNode(fullName);
            }
        }

        return new VariableNode(name);
    }


}
