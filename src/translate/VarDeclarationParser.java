package translate;

import ast.ASTNode;
import ast.lists.ListNode;
import ast.maps.DynamicMap;
import ast.maps.MapNode;
import ast.maps.MapParser;
import ast.lists.DynamicList;
import tokens.Token;
import ast.variables.VariableDeclarationNode;

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

        if (type.equals("List")) {
            return parseListDeclaration();
        } else if (type.equals("Map")) {
            if (parser.current().getValue().equals("=")) {
                parser.advance();
                MapParser mapParser = new MapParser(parser);
                initializer = mapParser.parseMapInitializer(); // lê apenas o { ... }
            } else {
                initializer = new MapNode(new DynamicMap());
            }
        } else {

            if (!type.equals("var")) {
                parser.declareVariableType(name, type);
            }

            if (parser.current().getValue().equals("=")) {
                parser.advance();
                initializer = parser.parseExpression();
            }
        }
        parser.eat(Token.TokenType.DELIMITER, ";");
        parser.declareVariableType(name, type); // declara sempre como String

        return new VariableDeclarationNode(name, type, initializer);
    }

    public ASTNode parseListDeclaration() {

        String elementType = parser.current().getValue(); // ex: int, string
        parser.advance();
        parser.eat(Token.TokenType.OPERATOR, ">");

        // Captura o nome da variável
        String varName = parser.current().getValue();
        parser.advance();

        DynamicList dynamicList;

        if (parser.current().getValue().equals("=")) {
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, "(");

            List<ASTNode> elements = new ArrayList<>();
            while (!parser.current().getValue().equals(")")) {
                ASTNode elementNode = parser.parseExpression();
                elements.add(elementNode);
                if (parser.current().getValue().equals(",")) parser.advance();
            }

            parser.eat(Token.TokenType.DELIMITER, ")");

            dynamicList = new DynamicList(elementType, elements);
        } else {
            dynamicList = new DynamicList(elementType, new ArrayList<>());
        }

        parser.eat(Token.TokenType.DELIMITER, ";");

        String fullType = "List<" + elementType + ">";
        parser.declareVariableType(varName, fullType);

        return new VariableDeclarationNode(varName, fullType, new ListNode(dynamicList));
    }
}