package ast.structs;

import ast.ASTNode;
import ast.variables.VariableDeclarationNode;
import tokens.Token;
import translate.Parser;

import java.util.ArrayList;
import java.util.List;

public class StructInstanceParser {
    private final Parser parser;

    public StructInstanceParser(Parser parser) {
        this.parser = parser;
    }

    public VariableDeclarationNode parseStructInstanceAfterKeyword(String structName) {
        String varName = parser.current().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);

        List<ASTNode> positionalValues = new ArrayList<>();

        if (parser.current().getType() == Token.TokenType.OPERATOR &&
                parser.current().getValue().equals("=")) {

            parser.eat(Token.TokenType.OPERATOR, "=");
            parser.eat(Token.TokenType.DELIMITER, "{");

            while (!parser.current().getValue().equals("}")) {
                ASTNode value = parser.parseExpression();
                positionalValues.add(value);

                if (parser.current().getValue().equals(",")) {
                    parser.eat(Token.TokenType.DELIMITER, ",");
                } else {
                    break;
                }
            }

            parser.eat(Token.TokenType.DELIMITER, "}");
        }

        parser.eat(Token.TokenType.DELIMITER, ";");

        StructInstaceNode instanceNode = new StructInstaceNode(structName, positionalValues);
        return new VariableDeclarationNode(varName, structName, instanceNode);
    }
}
