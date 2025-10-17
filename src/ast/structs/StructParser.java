package ast.structs;

import ast.imports.StructNode;
import ast.variables.VariableDeclarationNode;
import tokens.Token;
import translate.Parser;

import java.util.ArrayList;
import java.util.List;

public class StructParser {
    private final Parser parser;

    public StructParser(Parser parser) {
        this.parser = parser;
    }

    public StructNode parseStruct() {
        parser.eat(Token.TokenType.KEYWORD, "Struct");

        String structName = parser.current().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);

        parser.eat(Token.TokenType.DELIMITER, "{");

        List<VariableDeclarationNode> fields = new ArrayList<>();

        while (!parser.current().getValue().equals("}")) {

            String fieldType = parser.current().getValue();
            parser.eat(Token.TokenType.KEYWORD);


            String fieldName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);


            parser.eat(Token.TokenType.DELIMITER, ";");

            fields.add(new VariableDeclarationNode(fieldType, fieldName, null));
        }

        parser.eat(Token.TokenType.DELIMITER, "}");

        return new StructNode(structName, fields);
    }
}
