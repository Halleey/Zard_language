package ast.structs;

import ast.variables.VariableDeclarationNode;
import tokens.Token;
import translate.Parser;

public class StructInstanceParser {
    private final Parser parser;

    public StructInstanceParser(Parser parser) {
        this.parser = parser;
    }

    public VariableDeclarationNode parseStructInstanceAfterKeyword(String structName) {
        String varName = parser.current().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);
        parser.eat(Token.TokenType.DELIMITER, ";");

        StructInstaceNode instanceNode = new StructInstaceNode(structName, null);
        return new VariableDeclarationNode(varName, structName, instanceNode);
    }
}
