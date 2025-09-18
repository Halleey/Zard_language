package ast.maps;

import ast.ASTNode;
import tokens.Token;
import translate.Parser;

import java.util.LinkedHashMap;
import java.util.Map;
public class MapParser {

    private final Parser parser;

    public MapParser(Parser parser) {
        this.parser = parser;
    }

    public MapNode parseMapInitializer() {
        parser.eat(Token.TokenType.DELIMITER, "{");
        Map<ASTNode, ASTNode> entries = new LinkedHashMap<>();

        while (!parser.current().getValue().equals("}")) {
            ASTNode keyExpr = parser.parseExpression();
            parser.eat(Token.TokenType.DELIMITER, ":");
            ASTNode valueExpr = parser.parseExpression();
            entries.put(keyExpr, valueExpr);

            if (parser.current().getValue().equals(",")) parser.advance();
        }

        parser.eat(Token.TokenType.DELIMITER, "}");
        return new MapNode(new DynamicMap(entries));
    }

}
