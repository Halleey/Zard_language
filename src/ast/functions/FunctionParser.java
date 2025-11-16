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
        debug("INIT FunctionParser (implStructName=" + implStructName + ")");
    }

    private void debug(String msg) {
        System.out.println("[FunctionParser] " + msg);
    }

    private String parseType() {
        debug("parseType() INICIADO");
        StringBuilder typeBuilder = new StringBuilder();
        int angleBrackets = 0;

        do {
            Token tok = parser.current();
            debug("  Consumindo tipo token: " + tok.getValue());

            String val = tok.getValue();
            typeBuilder.append(val);

            if (val.equals("<")) angleBrackets++;
            if (val.equals(">")) angleBrackets--;

            parser.advance();

            if (angleBrackets == 0) {

                Token next = parser.current();
                debug("  Checando parada tipo. Próximo: " + next.getValue());

                if (next.getType() == Token.TokenType.IDENTIFIER ||
                        next.getType() == Token.TokenType.DELIMITER ||
                        next.getValue().equals("?")) {

                    debug("  finalizando parseType: " + typeBuilder);
                    break;
                }
            }
        } while (true);

        String type = typeBuilder.toString().trim();
        debug("parseType() → " + type);
        return type;
    }

    private boolean isPrimitive(String type) {
        return switch (type) {
            case "int", "double", "bool", "string", "char", "void" -> true;
            default -> false;
        };
    }

    private boolean isListType(String type) {

        return type != null && type.startsWith("List");
    }

    public FunctionNode parseFunction() {
        debug("==== parseFunction() INICIO ====");

        debug("Consumindo 'function'");
        parser.advance();

        String returnType = "?";

        Token cur = parser.current();
        debug("Token atual ao ler tipo retorno: " + cur.getValue());

        if (cur.getType() == Token.TokenType.KEYWORD
                || cur.getType() == Token.TokenType.IDENTIFIER
                || "?".equals(cur.getValue())) {

            debug("O token pode ser tipo de retorno. peek=" + parser.peek().getValue());

            if (!parser.peek().getValue().equals("(")) {
                returnType = parseType();
            }
        }

        debug("-> Tipo de retorno detectado: " + returnType);

        String funcName = parser.current().getValue();
        debug("Nome da função encontrado: " + funcName);
        parser.eat(Token.TokenType.IDENTIFIER);

        // Início dos parâmetros
        debug("Lendo parâmetros...");
        parser.eat(Token.TokenType.DELIMITER, "(");

        List<String> paramNames = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();

        while (!parser.current().getValue().equals(")")) {

            debug("Parsing tipo do próximo parâmetro, token=" + parser.current().getValue());
            StringBuilder typeBuilder = new StringBuilder();
            int angleBrackets = 0;

            do {
                Token t = parser.current();
                debug("  Tipo token: " + t.getValue());

                String val = t.getValue();
                typeBuilder.append(val);
                if (val.equals("<")) angleBrackets++;
                if (val.equals(">")) angleBrackets--;
                parser.advance();

            } while (angleBrackets > 0 ||
                    (parser.current().getType() != Token.TokenType.IDENTIFIER &&
                            !parser.current().getValue().equals(",") &&
                            !parser.current().getValue().equals(")"))
            );

            String type = typeBuilder.toString().trim();
            debug("  → Tipo detectado: " + type);

            String name = parser.current().getValue();
            debug("  → Nome do parâmetro: " + name);
            parser.eat(Token.TokenType.IDENTIFIER);

            paramTypes.add(type);
            paramNames.add(name);

            if (parser.current().getValue().equals(",")) {
                debug("  vírgula detectada, avançando...");
                parser.advance();
            }
        }

        debug("Parâmetros lidos: " + paramNames);
        parser.eat(Token.TokenType.DELIMITER, ")");

        debug("PUSH contexto local.");
        parser.pushContext();

        for (int i = 0; i < paramNames.size(); i++) {
            String type = paramTypes.get(i);

            debug("Declarando parâmetro explícito: " + paramNames.get(i)
                    + " : " + type);

            if (parser.lookupStruct(type) != null
                    && !type.startsWith("Struct<")
                    && !isPrimitive(type)
                    && !isListType(type)) { // <<<<<< ADICIONADO
                type = "Struct<" + type + ">";
                debug("  Tipo ajustado para: " + type);
            }

            parser.declareVariable(paramNames.get(i), type);
            paramTypes.set(i, type);
        }

        if (implStructName != null && !paramNames.contains("s")) {
            String receiverType = "Struct<" + implStructName + ">";
            debug("Injetando receiver implícito → s : " + receiverType);

            parser.declareVariable("s", receiverType);

        }

        // PARSEIA CORPO
        debug("Lendo corpo da função...");
        List<ASTNode> body = parser.parseBlock();

        debug("POP contexto.");
        parser.popContext();

        if (returnType == null || returnType.isBlank()) {
            returnType = "void";
        }

        debug("==== parseFunction() FIM ====");
        debug("Função construída: " + funcName + "(" + paramNames + ") -> " + returnType);

        return new FunctionNode(funcName, paramNames, paramTypes, body, returnType);
    }
}
