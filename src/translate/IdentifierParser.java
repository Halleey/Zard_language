package translate;

import ast.ASTNode;
import ast.lists.ListAddNode;
import ast.lists.ListClearNode;
import ast.lists.ListRemoveNode;
import ast.lists.ListSizeNode;
import tokens.Token;
import variables.AssignmentNode;
import variables.UnaryOpNode;
import variables.VariableNode;

public class IdentifierParser {
    private final Parser parser;

    public IdentifierParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseIdentifier(String name) {
        // Suporte para chamadas de métodos de lista
        if (parser.current().getValue().equals(".")) {
            parser.advance();
            String method = parser.current().getValue();
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, "(");

            ASTNode arg = null;
            if (!parser.current().getValue().equals(")")) {
                arg = parser.parseExpression();
            }

            parser.eat(Token.TokenType.DELIMITER, ")");
            parser.eat(Token.TokenType.DELIMITER, ";");

            ASTNode listVar = new VariableNode(name);

            return switch (method) {
                case "add" -> new ListAddNode(listVar, arg);
                case "clear" -> new ListClearNode(listVar);
                case "remove" -> new ListRemoveNode(listVar, arg);
                case "size" -> new ListSizeNode(listVar);
                default -> throw new RuntimeException("Método de lista desconhecido: " + method);
            };
        }

        // Atribuição normal
        if (parser.current().getValue().equals("=")) {
            parser.advance();
            ASTNode value = parser.parseExpression();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new AssignmentNode(name, value);
        }

        // Operadores unários
        if (parser.current().getValue().equals("++") || parser.current().getValue().equals("--")) {
            String op = parser.current().getValue();
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new UnaryOpNode(name, op);
        }

        throw new RuntimeException("Comando inesperado: " + name);
    }
}

