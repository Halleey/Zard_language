package translate;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.lists.ListAddNode;
import ast.lists.ListClearNode;
import ast.lists.ListRemoveNode;
import ast.lists.ListSizeNode;
import tokens.Token;
import variables.AssignmentNode;
import variables.UnaryOpNode;
import variables.VariableNode;

import java.util.List;

public class IdentifierParser {
    private final Parser parser;

    public IdentifierParser(Parser parser) {
        this.parser = parser;
    }

    // Chamado pelo parseStatement
    public ASTNode parseAsStatement(String name) {
        // lista
        if (parser.current().getValue().equals(".")) {
            return parseListStatement(name);
        }

        // atribuição
        if (parser.current().getValue().equals("=")) {
            parser.advance();
            ASTNode value = parser.parseExpression();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new AssignmentNode(name, value);
        }

        // unário ++ / --
        if (parser.current().getValue().equals("++") || parser.current().getValue().equals("--")) {
            String op = parser.current().getValue();
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new UnaryOpNode(name, op);
        }

        // variável sozinha → statement inútil
        parser.eat(Token.TokenType.DELIMITER, ";");
        return new VariableNode(name);
    }

    // Chamado pelo parseFactor (expressão)
    public ASTNode parseAsExpression(String name) {
        // chamada de função
        if (parser.current().getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            return new FunctionCallNode(name, args);
        }

        // métodos de lista válidos em expressão
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

            ASTNode listVar = new VariableNode(name);
            return switch (method) {
                case "size" -> new ListSizeNode(listVar);
                default -> throw new RuntimeException("Método de lista não permitido em expressão: " + method);
            };
        }

        return new VariableNode(name);
    }

    private ASTNode parseListStatement(String name) {
        parser.advance();
        String method = parser.current().getValue();
        parser.advance();
        parser.eat(Token.TokenType.DELIMITER, "(");

        ASTNode arg = null;
        if (!parser.current().getValue().equals(")")) {
            arg = parser.parseExpression();
        }
        parser.eat(Token.TokenType.DELIMITER, ")");
        ASTNode listVar = new VariableNode(name);

        return switch (method) {
            case "add" -> {
                if (arg == null) throw new RuntimeException("Método add requer argumento");
                ASTNode node = new ListAddNode(listVar, arg);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }
            case "remove" -> {
                if (arg == null) throw new RuntimeException("Método remove requer argumento");
                ASTNode node = new ListRemoveNode(listVar, arg);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }
            case "clear" -> {
                ASTNode node = new ListClearNode(listVar);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }
            default -> throw new RuntimeException("Método de lista inválido em statement: " + method);
        };
    }
}
