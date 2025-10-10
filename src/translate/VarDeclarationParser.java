package translate;

import ast.ASTNode;
import ast.lists.ListDeclarationParser;
import ast.lists.ListNode;
import ast.maps.DynamicMap;
import ast.maps.MapNode;
import ast.maps.MapParser;
import ast.lists.DynamicList;
import ast.variables.LiteralNode;
import ast.variables.VariableNode;
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

        ASTNode initializer = null;

        if (type.equals("List")) {
            ListDeclarationParser listParser = new ListDeclarationParser(parser);
            return listParser.parse(null); // nome será lido dentro
        }
        else if (type.equals("Map")) {
            MapParser mapParser = new MapParser(parser);
            return mapParser.parse(null);
        } else {
            String name = parser.current().getValue();
            parser.advance(); // consome nome

            if (!type.equals("var")) {
                parser.declareVariableType(name, type);
            }

            if (parser.current().getValue().equals("=")) {
                parser.advance();
                initializer = parser.parseExpression();
            }

            parser.eat(Token.TokenType.DELIMITER, ";");
            parser.declareVariableType(name, type);
            return new VariableDeclarationNode(name, type, initializer);
        }
    }
}
