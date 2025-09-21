package ast.maps;

import ast.ASTNode;
import tokens.Token;
import translate.Parser;
import ast.variables.VariableNode;

public class MapMethodParser {
    private final Parser parser;

    public MapMethodParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseStatementMapMethod(String name) {
        String method = parser.current().getValue();
        parser.advance();
        parser.eat(Token.TokenType.DELIMITER, "(");

        ASTNode key = null;
        ASTNode value = null;

        if (!parser.current().getValue().equals(")")) {
            key = parser.parseExpression();
            if (method.equals("put")) {
                parser.eat(Token.TokenType.DELIMITER, ",");
                value = parser.parseExpression();
            }
        }

        parser.eat(Token.TokenType.DELIMITER, ")");

        ASTNode mapVar = new VariableNode(name);

        ASTNode node = switch (method) {
            case "put" -> new MapPutNode(mapVar, key, value);
            case "remove" -> new MapRemoveNode(mapVar, key);
            default -> throw new RuntimeException("Método de map inválido em statement: " + method);
        };

        parser.eat(Token.TokenType.DELIMITER, ";");
        return node;
    }


    public ASTNode parseExpressionMapMethod(String name) {
        parser.eat(Token.TokenType.DELIMITER, ".");
        String method = parser.current().getValue();
        parser.advance(); // consome o nome do método
        parser.eat(Token.TokenType.DELIMITER, "(");

        ASTNode key = null;
        if (!parser.current().getValue().equals(")")) {
            key = parser.parseExpression();
        }

        parser.eat(Token.TokenType.DELIMITER, ")"); // fecha parêntese do método

        ASTNode mapVar = new VariableNode(name);

        return switch (method) {
            case "get" -> new MapGetNode(mapVar, key);
            // case "size" -> new MapSizeNode(mapVar); // futuramente
            default -> throw new RuntimeException("Método de map não permitido em expressão: " + method);
        };
    }
}
