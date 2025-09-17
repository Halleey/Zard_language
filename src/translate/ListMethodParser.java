package translate;

import ast.ASTNode;
import ast.lists.*;
import tokens.Token;
import variables.VariableNode;

public class ListMethodParser {
    private final Parser parser;

    public ListMethodParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseStatementListMethod(String name) {
        System.out.println("Current token: " + parser.current());
        String type = parser.getVariableType(name);
        System.out.println("DEBUG: Variável '" + name + "' tem tipo: " + type);

        // o '.' já foi consumido antes de chamar este método
        parser.eat(Token.TokenType.DELIMITER, "."); // consome o ponto
        String method = parser.current().getValue(); // pega o método

        Token.TokenType methodType = parser.current().getType();
        System.out.println("DEBUG: Método detectado: " + method + ", tipo do token: " + methodType);

        // avançar para '('
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
            case "addAll" -> {
                if (arg == null) throw new RuntimeException("Método addAll requer argumento");
                ASTNode node = new ListAddAllNode(listVar, arg);
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
        System.out.println("current token in process" + parser.current());
        // consome o '.'
        parser.eat(Token.TokenType.DELIMITER, ".");

        // agora pega o método
        String method = parser.current().getValue();
        parser.advance(); // consome o nome do método

        // consome '('
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
