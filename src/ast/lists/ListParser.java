package ast.lists;

import ast.ASTNode;
import tokens.Token;
import translate.Parser;
import ast.variables.VariableDeclarationNode;

import java.util.ArrayList;
import java.util.List;

public class ListParser {
    private final Parser parser;

    public ListParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseListDeclaration() {
        parser.advance(); // consome "list"
        String name = parser.current().getValue();
        parser.advance();

        ASTNode initializer;

        if (parser.current().getValue().equals("=")) {
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, "(");

            List<ASTNode> elements = new ArrayList<>();

            while (!parser.current().getValue().equals(")")) {
                ASTNode expr = parser.parseExpression();
                elements.add(expr); // armazena ASTNode, não TypedValue

                if (parser.current().getValue().equals(",")) {
                    parser.advance();
                }
            }

            parser.eat(Token.TokenType.DELIMITER, ")");
            initializer = new ListNode(new DynamicList(elements));
        } else {
            initializer = new ListNode(new DynamicList(new ArrayList<>()));
        }

        parser.eat(Token.TokenType.DELIMITER, ";");
        return new VariableDeclarationNode(name, "List", initializer);
    }

}