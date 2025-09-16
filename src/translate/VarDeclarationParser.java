package translate;

import ast.ASTNode;
import ast.lists.ListNode;
import expressions.DynamicList;
import tokens.Token;
import variables.VariableDeclarationNode;

import java.util.ArrayList;
import java.util.List;

public class VarDeclarationParser {
    private final Parser parser;

    public VarDeclarationParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseVarDeclaration() {
        String type = parser.current().getValue();
        parser.advance();
        String name = parser.current().getValue();
        parser.advance();

        ASTNode initializer = null;

        if (type.equals("list")) {
            if (parser.current().getValue().equals("=")) {
                parser.advance();
                parser.eat(Token.TokenType.DELIMITER, "(");
                List<ASTNode> elements = new ArrayList<>();
                while (!parser.current().getValue().equals(")")) {
                    elements.add(parser.parseExpression()); // ASTNode puro
                    if (parser.current().getValue().equals(",")) parser.advance();
                }
                parser.eat(Token.TokenType.DELIMITER, ")");
                initializer = new ListNode(new DynamicList(elements)); // só ASTNodes
            } else {
                initializer = new ListNode(new DynamicList(new ArrayList<>())); // lista vazia
            }

            parser.eat(Token.TokenType.DELIMITER, ";");
            return new VariableDeclarationNode(name, "list", initializer);
        } else {
            if (parser.current().getValue().equals("=")) {
                parser.advance();
                initializer = parser.parseExpression();
            }
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new VariableDeclarationNode(name, type, initializer);
        }
    }
}
