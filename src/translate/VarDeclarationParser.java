package translate;

import ast.ASTNode;
import ast.lists.ListNode;
import ast.maps.DynamicMap;
import ast.maps.MapNode;
import ast.maps.MapParser;
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

        // --- Listas ---
        if (type.equals("list")) {
            if (parser.current().getValue().equals("=")) {
                parser.advance();
                parser.eat(Token.TokenType.DELIMITER, "(");

                List<ASTNode> elements = new ArrayList<>();
                while (!parser.current().getValue().equals(")")) {
                    elements.add(parser.parseExpression());
                    if (parser.current().getValue().equals(",")) parser.advance();
                }

                parser.eat(Token.TokenType.DELIMITER, ")");
                initializer = new ListNode(new DynamicList(elements));
            } else {
                initializer = new ListNode(new DynamicList(new ArrayList<>()));
            }
        }

        else if (type.equals("map")) {
            if (parser.current().getValue().equals("=")) {
                parser.advance();
                MapParser mapParser = new MapParser(parser);
                initializer = mapParser.parseMapInitializer(); // lê apenas o { ... }
            } else {
                initializer = new MapNode(new DynamicMap());
            }
        }


        else {
            if (!type.equals("var")) {
                parser.declareVariableType(name, type);
            }

            if (parser.current().getValue().equals("=")) {
                parser.advance();
                initializer = parser.parseExpression();
            }
        }

        parser.eat(Token.TokenType.DELIMITER, ";");
        parser.declareVariableType(name, type);

        return new VariableDeclarationNode(name, type, initializer);
    }

}
