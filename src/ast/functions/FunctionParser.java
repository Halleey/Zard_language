package ast.functions;

import ast.ASTNode;
import tokens.Token;
import translate.front.Parser;

import java.util.ArrayList;
import java.util.List;
public class FunctionParser {

    private final Parser parser;
    private final String implStructName;

    public FunctionParser(Parser parser) {
        this(parser, null);
    }

    public FunctionParser(Parser parser, String implStructName) {
        this.parser = parser;
        this.implStructName = implStructName;
    }

    private String parseType() {
        StringBuilder sb = new StringBuilder();
        int depth = 0;

        do {
            Token tok = parser.current();
            sb.append(tok.getValue());

            if (tok.getValue().equals("<")) depth++;
            if (tok.getValue().equals(">")) depth--;

            parser.advance();

            if (depth == 0) {
                Token next = parser.current();
                if (next.getType() == Token.TokenType.IDENTIFIER ||
                        next.getType() == Token.TokenType.DELIMITER ||
                        next.getValue().equals("?")) {
                    break;
                }
            }

        } while (true);

        return sb.toString().trim();
    }

    private boolean isPrimitive(String t) {
        return switch (t) {
            case "int","double","bool","string","char","void" -> true;
            default -> false;
        };
    }

    private boolean isListType(String t) {
        return t != null && t.startsWith("List");
    }

    public FunctionNode parseFunction() {

        parser.advance();

        String returnType = "?";
        Token cur = parser.current();

        if (cur.getType() == Token.TokenType.KEYWORD
                || cur.getType() == Token.TokenType.IDENTIFIER
                || "?".equals(cur.getValue())) {

            if (!parser.peek().getValue().equals("(")) {
                returnType = parseType();
            }
        }

        String funcName = parser.current().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);
        parser.eat(Token.TokenType.DELIMITER, "(");

        List<String> paramNames = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();

        while (!parser.current().getValue().equals(")")) {

            StringBuilder typeBuilder = new StringBuilder();
            int depth = 0;

            do {
                Token t = parser.current();
                String val = t.getValue();

                typeBuilder.append(val);

                if (val.equals("<")) depth++;
                if (val.equals(">")) depth--;

                parser.advance();

            } while (depth > 0 ||
                    (parser.current().getType() != Token.TokenType.IDENTIFIER &&
                            !parser.current().getValue().equals(",") &&
                            !parser.current().getValue().equals(")")
                    ));

            String type = typeBuilder.toString().trim();
            String name = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);

            paramTypes.add(type);
            paramNames.add(name);

            if (parser.current().getValue().equals(",")) {
                parser.advance();
            }
        }

        parser.eat(Token.TokenType.DELIMITER, ")");

        parser.pushContext();

        // registrar parâmetros normais
        for (int i = 0; i < paramNames.size(); i++) {
            String type = paramTypes.get(i);

            if (parser.lookupStruct(type) != null
                    && !type.startsWith("Struct<")
                    && !isPrimitive(type)
                    && !isListType(type)) {
                type = "Struct<" + type + ">";
            }

            // *** CORREÇÃO PRINCIPAL ***
            if (type.equals("?")) {
                type = "i8*"; // tipo genérico default
            }

            parser.declareVariable(paramNames.get(i), type);
            paramTypes.set(i, type);
        }

        // registrar o "s" de métodos de impl
        if (implStructName != null) {
            String receiverType = "Struct<" + implStructName + ">";

            if (!paramNames.contains("s")) {
                paramNames.add(0, "s");
                paramTypes.add(0, receiverType);
                parser.declareVariable("s", receiverType);
            }
        }

        List<ASTNode> body = parser.parseBlock();

        parser.popContext();

        if (returnType == null || returnType.isBlank()) {
            returnType = "void";
        }

        return new FunctionNode(funcName, paramNames, paramTypes, body, returnType);
    }
}
