package translate.identifiers;

import ast.ASTNode;
import ast.expressions.CompoundParser;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.structs.*;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.ListType;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import helpers_ast.variables.AssignmentParser;
import helpers_ast.variables.UnaryParser;
import tokens.Token;
import ast.variables.VariableNode;
import translate.front.Parser;
import translate.identifiers.structs.StructSimpleDeclarationParser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IdentifierParser {

    private final Parser parser;

    public IdentifierParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseAsStatement(String name) {
        ASTNode receiver = new VariableNode(name);
        Token currentToken = parser.current();
        String structName = parseTypeStructName(name);

        // Struct conhecida
        if (parser.isKnownStruct(name)) {
            String varName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);

            // Inicialização com '='
            if (parser.current().getValue().equals("=")) {
                parser.eat(Token.TokenType.OPERATOR, "=");

                ASTNode initializer;
                if (parser.current().getValue().equals("{")) {
                    StructInstanceParser structParser = new StructInstanceParser(parser);
                    initializer = structParser.parseStructInstanceAfterKeyword(structName, varName);
                } else {
                    initializer = parser.parseExpression();
                }

                parser.declareVariable(varName, new StructType(structName));
                return new VariableDeclarationNode(varName, new StructType(structName), initializer);
            }

            // Instanciação direta com chaves
            if (parser.current().getValue().equals("{")) {
                StructInstanceParser structParser = new StructInstanceParser(parser);
                ASTNode initializer = structParser.parseStructInstanceAfterKeyword(structName, varName);
                parser.declareVariable(varName, new StructType(structName));
                return new VariableDeclarationNode(varName, new StructType(structName), initializer);
            }

            // Declaração simples sem inicializador
            parser.eat(Token.TokenType.DELIMITER, ";");
            StructInstanceNode instanceNode = new StructInstanceNode(structName, null, null);
            parser.declareVariable(varName, new StructType(structName));
            return new VariableDeclarationNode(varName, new StructType(structName), instanceNode);
        }

        // Declaração simples de Struct
        StructSimpleDeclarationParser simpleStructParser = new StructSimpleDeclarationParser(parser);
        ASTNode simpleDecl = simpleStructParser.tryParse(structName);
        if (simpleDecl != null) return simpleDecl;

        // Operações sobre '.' ou chamadas
        switch (currentToken.getValue()) {
            case "." -> {

                parser.advance(); // consome '.'

                String memberName = parser.current().getValue();
                parser.eat(Token.TokenType.IDENTIFIER);

                Type receiverType = parser.getExpressionType(receiver);

    /* =========================
       STRUCT ACCESS
    ========================= */
                if (receiverType instanceof StructType st) {

                    ASTNode structAccess =
                            new StructFieldAccessNode(receiver, memberName, null);

                    /* ---------- ATRIBUIÇÃO ---------- */
                    if (parser.current().getValue().equals("=")) {

                        parser.eat(Token.TokenType.OPERATOR, "=");

                        ASTNode value = parser.parseExpression();

                        parser.eat(Token.TokenType.DELIMITER, ";");

                        return new StructFieldAccessNode(receiver, memberName, value);
                    }

                    /* ---------- UPDATE INLINE ---------- */
                    if (parser.current().getValue().equals("{")) {
                        return parseInlineStructUpdate(structAccess);
                    }

                    /* ---------- METHOD CALL ---------- */
                    if (parser.current().getValue().equals("(")) {

                        List<ASTNode> args = parser.parseArguments();
                        parser.eat(Token.TokenType.DELIMITER, ";");

                        return new StructMethodCallNode(
                                receiver,
                                st.name(),
                                memberName,
                                args
                        );
                    }

                    /* ---------- ENCADEAMENTO ---------- */
                    if (parser.current().getValue().equals(".")) {

                        parser.advance();

                        String nextMember = parser.current().getValue();
                        parser.eat(Token.TokenType.IDENTIFIER);

                        Type nextType = parser.getExpressionType(structAccess);

                        /* ----- LIST METHOD ----- */
                        if (nextType instanceof ListType) {

                            ListMethodParser listParser = new ListMethodParser(parser);

                            return listParser.parseStatementListMethod(structAccess, nextMember);
                        }

                        /* ----- NESTED STRUCT FIELD ----- */
                        if (nextType instanceof StructType) {

                            ASTNode nestedAccess =
                                    new StructFieldAccessNode(structAccess, nextMember, null);

                            if (parser.current().getValue().equals("=")) {

                                parser.eat(Token.TokenType.OPERATOR, "=");

                                ASTNode value = parser.parseExpression();

                                parser.eat(Token.TokenType.DELIMITER, ";");

                                return new StructFieldAccessNode(nestedAccess, nextMember, value);
                            }

                            parser.eat(Token.TokenType.DELIMITER, ";");

                            return nestedAccess;
                        }
                    }

                    parser.eat(Token.TokenType.DELIMITER, ";");

                    return structAccess;
                }

    /* =========================
       LIST ACCESS
    ========================= */
                if (receiverType instanceof ListType) {

                    ASTNode listAccess =
                            new StructFieldAccessNode(receiver, memberName, null);

                    ListMethodParser listParser = new ListMethodParser(parser);

                    return listParser.parseStatementListMethod(listAccess, memberName);
                }

    /* =========================
       MODULE / NAMESPACE
    ========================= */

                parser.advance();

                if (parser.current().getValue().equals("(")) {

                    List<ASTNode> args = parser.parseArguments();

                    if (parser.current().getValue().equals(";")) {
                        parser.eat(Token.TokenType.DELIMITER, ";");
                    }

                    return new FunctionCallNode(name + "." + memberName, args);

                } else {

                    if (parser.current().getValue().equals(";")) {
                        parser.eat(Token.TokenType.DELIMITER, ";");
                    }

                    return new VariableNode(name + "." + memberName);
                }
            }

            case "=" -> {
                return new AssignmentParser(parser).parse(name);
            }

            case "++", "--" -> {
                return new UnaryParser(parser).parser(name, currentToken.getValue());
            }

            case "+=", "-=" -> {
                return new CompoundParser(parser).parse(name, currentToken.getValue());
            }
        }

        return receiver;
    }
    private StructUpdateNode parseInlineStructUpdate(ASTNode target) {

        parser.eat(Token.TokenType.DELIMITER, "{");

        Map<String, ASTNode> fieldUpdates = new LinkedHashMap<>();
        Map<String, StructUpdateNode> nestedUpdates = new LinkedHashMap<>();

        while (!parser.current().getValue().equals("}")) {

            String fieldName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);

            if (parser.current().getValue().equals(":")) {

                parser.advance();
                ASTNode value = parser.parseExpression();
                parser.eat(Token.TokenType.DELIMITER, ";");

                fieldUpdates.put(fieldName, value);
                continue;
            }

            if (parser.current().getValue().equals("{")) {

                ASTNode nestedTarget =
                        new StructFieldAccessNode(target, fieldName, null);

                StructUpdateNode nested =
                        parseInlineStructUpdate(nestedTarget);

                nestedUpdates.put(fieldName, nested);
                continue;
            }

            throw new RuntimeException(
                    "Esperado ':' ou '{' após nome do campo"
            );
        }

        parser.eat(Token.TokenType.DELIMITER, "}");

        return new StructUpdateNode(target, fieldUpdates, nestedUpdates);
    }

    public ASTNode parseAsExpression(String name) {
        ASTNode node = new VariableNode(name);

        // chamada direta: f(...)
        if (parser.current().getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            return new FunctionCallNode(name, args);
        }

        // encadeamento
        while (parser.current().getValue().equals(".")) {
            parser.advance(); // '.'
            String memberName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);

            Type receiverType = parser.getExpressionType(node);

            if (receiverType instanceof StructType st) {
                if (parser.current().getValue().equals("(")) {
                    List<ASTNode> args = parser.parseArguments();
                    StructMethodCallNode call = new StructMethodCallNode(node, st.name(), memberName, args);
                    FunctionNode method = parser.getStructMethod(st.name(), memberName);
                    call.setReturnType(method.getReturnType());
                    node = call;
                    continue;
                }
                node = new StructFieldAccessNode(node, memberName, null);
                continue;
            }

            if (receiverType instanceof ListType) {
                ListMethodParser listParser = new ListMethodParser(parser);
                node = listParser.parseExpressionListMethod(node, memberName);
                continue;
            }

            if (parser.current().getValue().equals("(")) {
                List<ASTNode> args = parser.parseArguments();
                node = new FunctionCallNode(node.toString() + "." + memberName, args);
            } else {
                node = new StructFieldAccessNode(node, memberName, null);
            }
        }

        return node;
    }

    private String parseTypeStructName(String baseName) {
        if (!parser.current().getValue().equals("<")) {
            return baseName;
        }

        parser.advance();
        Token typeToken = parser.current();

        if (typeToken.getType() != Token.TokenType.IDENTIFIER &&
                typeToken.getType() != Token.TokenType.KEYWORD) {
            throw new RuntimeException("Tipo inválido em especialização de struct: " + typeToken);
        }

        String innerType = typeToken.getValue();
        parser.advance();
        parser.eat(Token.TokenType.OPERATOR, ">");
        return baseName + "<" + innerType + ">";
    }
}