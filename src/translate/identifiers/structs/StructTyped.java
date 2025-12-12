package translate.identifiers.structs;

import tokens.Token;
import translate.front.Parser;

public class StructTyped {

    private final Parser parser;

    public StructTyped(Parser parser) {
        this.parser = parser;
    }

    public String parseIfSpecialized(String baseName) {

        if (!parser.current().getValue().equals("<")) {
            return baseName;
        }

        parser.advance();

        String innerType = parser.current().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);

        parser.eat(Token.TokenType.OPERATOR, ">");

        return baseName + "<" + innerType + ">";
    }
}
