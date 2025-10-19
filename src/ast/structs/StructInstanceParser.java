package ast.structs;

import ast.ASTNode;
import ast.variables.VariableDeclarationNode;
import tokens.Token;
import translate.Parser;

import java.util.ArrayList;
import java.util.List;
public class StructInstanceParser {
    private final Parser parser;

    public StructInstanceParser(Parser parser) {
        this.parser = parser;
    }

    // Agora recebe também o varName já consumido pelo IdentifierParser
    public VariableDeclarationNode parseStructInstanceAfterKeyword(String structName, String varName) {
        List<ASTNode> positionalValues = new ArrayList<>();

        // Checa inicialização direta (= { ... })
        if (parser.current().getType() == Token.TokenType.OPERATOR &&
                parser.current().getValue().equals("=")) {

            parser.eat(Token.TokenType.OPERATOR, "=");
            parser.eat(Token.TokenType.DELIMITER, "{");

            while (!parser.current().getValue().equals("}")) {
                ASTNode value = parser.parseExpression();
                positionalValues.add(value);

                if (parser.current().getValue().equals(",")) {
                    parser.eat(Token.TokenType.DELIMITER, ",");
                } else {
                    break;
                }
            }

            parser.eat(Token.TokenType.DELIMITER, "}");
        }

        // Final obrigatório ;
        parser.eat(Token.TokenType.DELIMITER, ";");

        // Cria nó de instância da struct
        StructInstaceNode instanceNode = new StructInstaceNode(structName, positionalValues);

        parser.declareVariable(varName, "Struct<" + structName + ">");

        return new VariableDeclarationNode(varName, "Struct<" + structName + ">", instanceNode);
    }

    // Mantém suporte a inline { ... } já passando structName e varName
    public VariableDeclarationNode parseStructInline(String structName, String varName) {
        List<ASTNode> positionalValues = new ArrayList<>();

        parser.eat(Token.TokenType.DELIMITER, "{");

        while (!parser.current().getValue().equals("}")) {
            ASTNode value = parser.parseExpression();
            positionalValues.add(value);

            if (parser.current().getValue().equals(",")) {
                parser.eat(Token.TokenType.DELIMITER, ",");
            } else {
                break;
            }
        }

        parser.eat(Token.TokenType.DELIMITER, "}");
        parser.eat(Token.TokenType.DELIMITER, ";");

        StructInstaceNode instanceNode = new StructInstaceNode(structName, positionalValues);

        parser.declareVariable(varName, "Struct<" + structName + ">");

        return new VariableDeclarationNode(varName, "Struct<" + structName + ">", instanceNode);
    }
}
