package ast.functions;

import ast.ASTNode;
import ast.variables.TypeResolver;
import context.statics.symbols.*;
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

    private Type parseType() {
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

        String typeName = sb.toString().trim();

        return TypeResolver.resolve(typeName);
    }

    private boolean isPrimitive(Type t) {
        return t instanceof PrimitiveTypes;
    }

    private boolean isListType(Type t) {
        return t instanceof ListType;
    }

    public FunctionNode parseFunction() {

        parser.advance(); // consome 'func' ou equivalente

        Type returnType = UnknownType.UNKNOWN_TYPE; // default
        Token cur = parser.current();

        // detecta tipo de retorno
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

            Type paramType = parseType();
            String paramName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);

            // ajusta struct genérica se necessário
            if (paramType instanceof UnknownType &&
                    parser.lookupStruct(paramName) != null) {
                paramType = new StructType(paramName);
            }

            // registra no escopo para o corpo da função
            parser.declareVariable(paramName, paramType);

            params.add(new ParamInfo(paramName, paramType, isRef));

            if (parser.current().getValue().equals(",")) {
                parser.advance();
            }
        }

        parser.eat(Token.TokenType.DELIMITER, ")");

        // receiver para métodos de struct
        if (implStructName != null) {
            Type receiverType = new StructType(implStructName);
            boolean hasS = params.stream().anyMatch(p -> p.name().equals("s"));
            if (!hasS) {
                parser.declareVariable("s", receiverType);
                params.add(0, new ParamInfo("s", receiverType, false));
            }
        }

        List<ASTNode> body = parser.parseBlock();

        parser.popContext();

        // tipo de retorno padrão
        if (returnType instanceof UnknownType) {
            returnType = PrimitiveTypes.VOID;
        }

        return new FunctionNode(funcName, params, body, returnType);
    }
}