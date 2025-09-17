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

            parser.eat(Token.TokenType.DELIMITER, ")"); // apenas fecha o parêntese

            ASTNode listVar = new VariableNode(name);

            return switch (method) {
                case "add" -> {
                    if (arg == null) throw new RuntimeException("Método add requer um argumento");
                    ASTNode node = new ListAddNode(listVar, arg);
                    parser.eat(Token.TokenType.DELIMITER, ";"); // statement
                    yield node;
                }
                case "clear" -> {
                    ASTNode node = new ListClearNode(listVar);
                    parser.eat(Token.TokenType.DELIMITER, ";"); // statement
                    yield node;
                }
                case "remove" -> {
                    if (arg == null) throw new RuntimeException("Método remove requer um argumento");
                    ASTNode node = new ListRemoveNode(listVar, arg);
                    parser.eat(Token.TokenType.DELIMITER, ";"); // statement
                    yield node;
                }
                case "size" -> new ListSizeNode(listVar); // expressão, ; será consumido pelo PrintParser
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

        // Apenas acessar a variável
        return new VariableNode(name);
    }
}
