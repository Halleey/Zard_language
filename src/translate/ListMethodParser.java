package translate;

import ast.ASTNode;
import ast.lists.*;
import ast.structs.StructFieldAccessNode;
import ast.variables.VariableDeclarationNode;
import tokens.Token;
import ast.variables.VariableNode;


import java.util.List;
public class ListMethodParser {
    private final Parser parser;

    public ListMethodParser(Parser parser) {
        this.parser = parser;
    }

    private ASTNode consumeArg() {
        parser.advance(); // consome nome do método já lido
        parser.eat(Token.TokenType.DELIMITER, "(");

        ASTNode arg = null;
        if (!parser.current().getValue().equals(")")) {
            arg = parser.parseExpression();
        }

        parser.eat(Token.TokenType.DELIMITER, ")");
        return arg;
    }

    // === Statements ===
    public ASTNode parseStatementListMethod(ASTNode receiver, String method) {
        ASTNode arg;

        return switch (method) {
            case "add" -> {
                arg = consumeArg();
                if (arg == null) throw new RuntimeException("Método add requer argumento");

                // resolve tipo do elemento
                String elementType = "unknown";
                String listType = parser.getExpressionType(receiver);
                if (listType != null && listType.startsWith("List<") && listType.endsWith(">")) {
                    elementType = listType.substring(5, listType.length() - 1);
                }

                ASTNode node = new ListAddNode(receiver, arg, elementType);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }

            case "addAll" -> {
                parser.advance(); // consome método
                ExpressionParser exprParser = new ExpressionParser(parser);
                List<ASTNode> argsList = exprParser.parseArguments();
                ASTNode node = new ListAddAllNode(receiver, argsList);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }

            case "remove" -> {
                arg = consumeArg();
                if (arg == null) throw new RuntimeException("Método remove requer argumento");
                ASTNode node = new ListRemoveNode(receiver, arg);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }

            case "clear" -> {
                parser.advance(); // consome método
                parser.eat(Token.TokenType.DELIMITER, "(");
                ASTNode node = new ListClearNode(receiver);
                parser.eat(Token.TokenType.DELIMITER, ")");
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }

            default -> throw new RuntimeException("Método de lista inválido em statement: " + method);
        };
    }

    // === Expressions ===
    public ASTNode parseExpressionListMethod(ASTNode receiver, String method) {
        parser.advance(); // consome nome do método
        parser.eat(Token.TokenType.DELIMITER, "(");

        ASTNode arg = null;
        if (!parser.current().getValue().equals(")")) {
            arg = parser.parseExpression();
        }

        parser.eat(Token.TokenType.DELIMITER, ")");

        ASTNode node;
        switch (method) {
            case "size" -> node = new ListSizeNode(receiver);
            case "get" -> {
                if (arg == null) throw new RuntimeException("get requer índice");
                node = new ListGetNode(receiver, arg);
            }
            default -> throw new RuntimeException("Método de lista não permitido em expressão: " + method);
        }

        // suporte a encadeamento: ex. lista.get(0).campo
        while (parser.current().getValue().equals(".")) {
            parser.advance();
            String memberName = parser.current().getValue();
            parser.advance();

            node = new StructFieldAccessNode(node, memberName, null);
        }

        return node;
    }
}
