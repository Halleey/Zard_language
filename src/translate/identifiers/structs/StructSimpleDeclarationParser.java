package translate.identifiers.structs;

import ast.ASTNode;
import ast.variables.VariableDeclarationNode;
import tokens.Token;
import translate.front.Parser;

public class StructSimpleDeclarationParser {

    private final Parser parser;

    public StructSimpleDeclarationParser(Parser parser) {
        this.parser = parser;
    }


    public ASTNode tryParse(String structName) {

        if (parser.current().getType() != Token.TokenType.IDENTIFIER) {
            return null;
        }

        String varName = parser.current().getValue();
        System.out.println("[StructSimpleDecl] VARNAME " + varName);

        Token afterVar = parser.peek(1);

        if (afterVar.getType() == Token.TokenType.DELIMITER &&
                afterVar.getValue().equals(";")) {

            parser.eat(Token.TokenType.IDENTIFIER);
            parser.eat(Token.TokenType.DELIMITER, ";");

            String finalType = "Struct<" + structName + ">";
            parser.declareVariable(varName, finalType);

            return new VariableDeclarationNode(varName, finalType, null);
        }

        return null;
    }
}
