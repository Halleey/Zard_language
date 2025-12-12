package translate.identifiers.structs;
import ast.ASTNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructInstanceParser;
import ast.variables.VariableDeclarationNode;
import tokens.Token;
import translate.front.Parser;

public class StructVariableDeclarationParser {

    private final Parser parser;

    public StructVariableDeclarationParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parse(String structName) {

        String varName = parser.current().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);

        if (parser.current().getValue().equals("=")) {
            parser.eat(Token.TokenType.OPERATOR, "=");

            if (parser.current().getValue().equals("{")) {
                StructInstanceParser sip =
                        new StructInstanceParser(parser);
                return sip.parseStructInstanceAfterKeyword(
                        structName,
                        varName
                );
            }

            ASTNode initializer = parser.parseExpression();
            parser.eat(Token.TokenType.DELIMITER, ";");

            parser.declareVariable(
                    varName,
                    "Struct<" + structName + ">"
            );

            return new VariableDeclarationNode(
                    varName,
                    "Struct<" + structName + ">",
                    initializer
            );
        }

        if (parser.current().getValue().equals("{")) {
            StructInstanceParser sip =
                    new StructInstanceParser(parser);
            return sip.parseStructInstanceAfterKeyword(
                    structName,
                    varName
            );
        }

        parser.eat(Token.TokenType.DELIMITER, ";");

        StructInstaceNode empty =
                new StructInstaceNode(structName, null, null);

        parser.declareVariable(
                varName,
                "Struct<" + structName + ">"
        );

        return new VariableDeclarationNode(
                varName,
                "Struct<" + structName + ">",
                empty
        );
    }
}