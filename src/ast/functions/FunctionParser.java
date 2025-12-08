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
                        "?".equals(next.getValue())) {
                    break;
                }
            }

        } while (true);

        return sb.toString().trim();
    }

    private boolean isPrimitive(String t) {
        return switch (t) {
            case "int", "double", "bool", "string", "char", "void" -> true;
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

        List<ParamInfo> params = new ArrayList<>();

        parser.pushContext();

        while (!parser.current().getValue().equals(")")) {

            boolean isRef = false;

            if (parser.current().getType() == Token.TokenType.OPERATOR
                    && parser.current().getValue().equals("&")) {
                isRef = true;
                parser.advance();
            }

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

            if (parser.lookupStruct(type) != null
                    && !type.startsWith("Struct<")
                    && !isPrimitive(type)
                    && !isListType(type)) {
                type = "Struct<" + type + ">";
            }

            if ("?".equals(type)) {
                type = "i8*"; // tipo genérico default
            }

            // registra no escopo do parser para o corpo da função
            parser.declareVariable(name, type);

            params.add(new ParamInfo(name, type, isRef));

            if (parser.current().getValue().equals(",")) {
                parser.advance();
            }
        }

        parser.eat(Token.TokenType.DELIMITER, ")");

        // registrar o "s" de métodos de impl (receiver), por valor
        if (implStructName != null) {
            String receiverType = "Struct<" + implStructName + ">";

            boolean hasS = params.stream().anyMatch(p -> p.name().equals("s"));
            if (!hasS) {
                parser.declareVariable("s", receiverType);
                params.add(0, new ParamInfo("s", receiverType, false));
            }
        }

        List<ASTNode> body = parser.parseBlock();

        parser.popContext();

        if (returnType == null || returnType.isBlank()) {
            returnType = "void";
        }

        return new FunctionNode(funcName, params, body, returnType);
    }
}
