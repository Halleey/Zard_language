package ast.functions;

import ast.ASTNode;
import tokens.Token;
import translate.Parser;

import java.util.ArrayList;
import java.util.List;

public class FunctionParser {
    private final Parser parser;

    public FunctionParser(Parser parser) {
        this.parser = parser;
    }

    private String parseType() {
        StringBuilder typeBuilder = new StringBuilder();
        int angleBrackets = 0;
        do {
            String val = parser.current().getValue();
            typeBuilder.append(val);

            if (val.equals("<")) angleBrackets++;
            if (val.equals(">")) angleBrackets--;

            parser.advance();

            if (angleBrackets == 0) {
                if (parser.current().getType() == Token.TokenType.IDENTIFIER
                        || parser.current().getType() == Token.TokenType.DELIMITER) {
                    break;
                }
            }
        } while (true);

        return typeBuilder.toString();
    }

    private boolean isPrimitive(String type) {
        return switch (type) {
            case "int", "double", "bool", "string", "char", "void" -> true;
            default -> false;
        };
    }

    public FunctionNode parseFunction() {
        parser.advance();

        String returnType = "void";
        if (parser.current().getType() == Token.TokenType.KEYWORD ||
                parser.current().getType() == Token.TokenType.IDENTIFIER) {

            returnType = parseType();
        }

        String funcName = parser.current().getValue();
        parser.advance();

        parser.eat(Token.TokenType.DELIMITER, "(");
        List<String> paramNames = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();

        while (!parser.current().getValue().equals(")")) {
            StringBuilder typeBuilder = new StringBuilder();
            int angleBrackets = 0;

            do {
                String val = parser.current().getValue();
                typeBuilder.append(val);
                if (val.equals("<")) angleBrackets++;
                if (val.equals(">")) angleBrackets--;
                parser.advance();
            } while (angleBrackets > 0 ||
                    (parser.current().getType() != Token.TokenType.IDENTIFIER && !parser.current().getValue().equals(",")));

            String type = typeBuilder.toString();

            String name = parser.current().getValue();
            parser.advance();

            paramTypes.add(type);
            paramNames.add(name);

            if (parser.current().getValue().equals(",")) {
                parser.advance();
            }
        }

        parser.eat(Token.TokenType.DELIMITER, ")");

        parser.pushContext();
        for (int i = 0; i < paramNames.size(); i++) {
            String type = paramTypes.get(i);

            if (parser.lookupStruct(type) != null
                    && !type.startsWith("Struct<")
                    && !isPrimitive(type)) {
                type = "Struct<" + type + ">";
            }

            parser.declareVariable(paramNames.get(i), type);
            paramTypes.set(i, type);
        }

        List<ASTNode> body = parser.parseBlock();
        parser.popContext();

        return new FunctionNode(funcName, paramNames, paramTypes, body, returnType);
    }
}
