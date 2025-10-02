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
            // Delegamos toda a lógica de listas para o método específico
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
            // Declaração normal
            if (!type.equals("var")) {
                parser.declareVariableType(name, type); // sempre String
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

        // Captura o tipo da lista dentro de <>
        System.out.println("debug -----" + parser.current());
        String elementType = parser.current().getValue(); // tipo da lista, ex: int, string
        parser.advance();
        parser.eat(Token.TokenType.OPERATOR, ">");

        // Captura o nome da variável
        String varName = parser.current().getValue();
        parser.advance();

        DynamicList dynamicList;

        // Inicialização opcional
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

        // Declara a variável como String "list" no parser/contexto
        parser.declareVariableType(varName, "List");

        // Retorna o nó de declaração com ListNode encapsulando DynamicList
        return new VariableDeclarationNode(varName, "List", new ListNode(dynamicList));
    }
}
