package translate;

import ast.ASTNode;
import ast.lists.ListAddNode;
import ast.lists.ListClearNode;
import ast.lists.ListRemoveNode;
import ast.lists.ListSizeNode;
import tokens.Token;
import variables.VariableNode;

public class ListMethodParser {
    private final Parser parser;

    public ListMethodParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseStatementListMethod(String name) {
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

    public ASTNode parseExpressionListMethod(String name) {
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
}
