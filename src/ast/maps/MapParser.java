package ast.maps;

import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import tokens.Token;
import translate.Parser;

public class MapParser {

    private final Parser parser;

    public MapParser(Parser parser) {
        this.parser = parser;
    }

    public MapNode parseMapInitializer() {
        parser.eat(Token.TokenType.DELIMITER, "{");
        DynamicMap dynamicMap = new DynamicMap();

        while (!parser.current().getValue().equals("}")) {
            TypedValue keyVal = parser.parseExpression().evaluate(new RuntimeContext());
            parser.eat(Token.TokenType.DELIMITER, ":");
            TypedValue valueVal = parser.parseExpression().evaluate(new RuntimeContext());
            dynamicMap.put(keyVal, valueVal);

            if (parser.current().getValue().equals(",")) parser.advance();
        }

        parser.eat(Token.TokenType.DELIMITER, "}");
        return new MapNode(dynamicMap);
    }
}
