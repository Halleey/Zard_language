package translate;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionReferenceNode;
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
            return new FunctionCallNode(name, args); // suporta namespaces dentro do name
        }

        // Suporte a namespace: Math.fatorial (referência, sem chamada)
        if (parser.current().getValue().equals(".")) {
            parser.advance();
            String memberName = parser.current().getValue();
            parser.advance();

            String fullName = name + "." + memberName;

            if (parser.current().getValue().equals("(")) {
                List<ASTNode> args = parser.parseArguments();
                return new FunctionCallNode(fullName, args); // chama a função no namespace
            }

            // apenas referência à função no namespace
            return new FunctionReferenceNode(fullName); // <--- aqui!
        }

        return new VariableNode(name);
    }

}
