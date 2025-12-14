package translate.identifiers;

import ast.ASTNode;
import ast.lists.*;
import ast.structs.StructFieldAccessNode;
import ast.variables.ExpressionParser;
import tokens.Token;
import translate.front.Parser;


import java.util.List;
public class ListMethodParser {
    private final Parser parser;

    public ListMethodParser(Parser parser) {
        this.parser = parser;
    }

    private void dbg(String msg) {
        System.out.println("[ListMethodParser] " + msg +
                " | current=" + parser.current());
    }

    private ASTNode consumeArg() {
        dbg("consumeArg() start");

        parser.advance(); // método
        dbg("after method advance");

        parser.eat(Token.TokenType.DELIMITER, "(");
        dbg("after '('");

        ASTNode arg = null;
        if (!parser.current().getValue().equals(")")) {
            dbg("parsing argument expression");
            arg = parser.parseExpression();
        }

        parser.eat(Token.TokenType.DELIMITER, ")");
        dbg("after ')'");

        return arg;
    }

    public ASTNode parseStatementListMethod(ASTNode receiver, String method) {
        dbg("parseStatementListMethod method=" + method +
                " receiverType=" + parser.getExpressionType(receiver));

        return switch (method) {

            case "add" -> {
                ASTNode arg = consumeArg();
                dbg("add arg=" + arg);

                if (arg == null) {
                    throw new RuntimeException("add requer exatamente 1 argumento");
                }

                String elementType = "unknown";
                String listType = parser.getExpressionType(receiver);
                dbg("listType=" + listType);

                if (listType != null && listType.startsWith("List<")) {
                    elementType = listType.substring(5, listType.length() - 1);
                }

                ASTNode node = new ListAddNode(receiver, arg, elementType);
                parser.eat(Token.TokenType.DELIMITER, ";");
                dbg("add statement finished");

                yield node;
            }

            case "addAll" -> {
                parser.advance();
                dbg("addAll advance");

                ExpressionParser exprParser = new ExpressionParser(parser);
                List<ASTNode> argsList = exprParser.parseArguments();

                dbg("addAll args count=" + argsList.size());

                ASTNode node = new ListAddAllNode(receiver, argsList);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }

            case "remove" -> {
                ASTNode arg = consumeArg();
                dbg("remove arg=" + arg);

                if (arg == null)
                    throw new RuntimeException("remove requer argumento");

                ASTNode node = new ListRemoveNode(receiver, arg);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }

            case "clear" -> {
                parser.advance();
                dbg("clear advance");

                parser.eat(Token.TokenType.DELIMITER, "(");
                parser.eat(Token.TokenType.DELIMITER, ")");

                ASTNode node = new ListClearNode(receiver);
                parser.eat(Token.TokenType.DELIMITER, ";");
                yield node;
            }

            default -> throw new RuntimeException(
                    "Método de lista inválido em statement: " + method);
        };
    }

    public ASTNode parseExpressionListMethod(ASTNode receiver, String method) {

        ASTNode arg = null;

        // se houver parênteses, delega corretamente
        if (parser.current().getValue().equals("(")) {
            parser.eat(Token.TokenType.DELIMITER, "(");

            if (!parser.current().getValue().equals(")")) {
                arg = parser.parseExpression();
            }

            parser.eat(Token.TokenType.DELIMITER, ")");
        }

        return switch (method) {
            case "size" -> new ListSizeNode(receiver);
            case "get" -> {
                if (arg == null)
                    throw new RuntimeException("get requer índice");
                yield new ListGetNode(receiver, arg);
            }
            default -> throw new RuntimeException(
                    "Método de lista não permitido em expressão: " + method);
        };
    }

}
