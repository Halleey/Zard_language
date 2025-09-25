package translate;

import ast.ASTNode;
import ast.lists.*;
import tokens.Token;
import ast.variables.VariableNode;


import java.util.List;

public class ListMethodParser {
    private final Parser parser;

    public ListMethodParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode consumer() {
        parser.advance(); // consome o nome do método
        parser.eat(Token.TokenType.DELIMITER, "(");

        ASTNode arg = null;
        if (!parser.current().getValue().equals(")")) {
            arg = parser.parseExpression();
        }

        parser.eat(Token.TokenType.DELIMITER, ")");
        return arg;
    }


    public ASTNode parseStatementListMethod(String name) {
        String method = parser.current().getValue();
        ASTNode listVar = new VariableNode(name);
        ASTNode arg;

        return switch (method) {
            case "add" -> {
                arg = consumer();
                if (arg == null) throw new RuntimeException("Método add requer argumento");
                ASTNode node = new ListAddNode(listVar, arg);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }
            case "addAll" -> {
                parser.advance();
                ExpressionParser exprParser = new ExpressionParser(parser);
                List<ASTNode> argsList = exprParser.parseArguments();
                ASTNode node = new ListAddAllNode(listVar, argsList);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }
            case "remove" -> {
                arg = consumer();
                if (arg == null) throw new RuntimeException("Método remove requer argumento");
                ASTNode node = new ListRemoveNode(listVar, arg);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }
            case "clear" -> {
                parser.advance(); // consome '('
                parser.eat(Token.TokenType.DELIMITER, ")");
                ASTNode node = new ListClearNode(listVar);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }
            default -> throw new RuntimeException("Método de lista inválido em statement: " + method);
        };
    }


    public ASTNode parseExpressionListMethod(String name) {
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
            case "get" -> {
                if (arg == null) throw new RuntimeException("get requer índice");
                yield new ListGetNode(listVar, arg);
            }
            default -> throw new RuntimeException("Método de lista não permitido em expressão: " + method);
        };
    }
}
