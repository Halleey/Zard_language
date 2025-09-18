package translate;

import ast.ASTNode;
import ast.lists.*;
import tokens.Token;
import variables.VariableNode;

import java.util.ArrayList;
import java.util.List;

public class ListMethodParser {
    private final Parser parser;

    public ListMethodParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseStatementListMethod(String name) {
        String type = parser.getVariableType(name);
        System.out.println("DEBUG: Variável '" + name + "' tem tipo: " + type);

        String method = parser.current().getValue();

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
                List<ASTNode> argsList = new ArrayList<>();

                // enquanto não fechar o parêntese, parseia os argumentos separados por ','
                while (!parser.current().getValue().equals(")")) {
                    ASTNode expr = parser.parseExpression();
                    argsList.add(expr);

                    if (parser.current().getValue().equals(",")) {
                        parser.advance(); // consome a vírgula
                    } else {
                        break;
                    }
                }

                parser.eat(Token.TokenType.DELIMITER, ")"); // fecha o parêntese

                ASTNode node = new ListAddAllNode(listVar, argsList);
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
            case "remove" -> {
                yield new ListRemoveNode(listVar, arg);
            }
            case "slice" -> {
                if (arg == null) throw new RuntimeException("slice requer índice");
                yield new ListSliceNode(listVar, arg);
            }

            default -> throw new RuntimeException("Método de lista não permitido em expressão: " + method);
        };
    }
}
