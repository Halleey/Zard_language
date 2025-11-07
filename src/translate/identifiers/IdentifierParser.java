package translate.identifiers;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionReferenceNode;
import ast.structs.StructInstanceParser;
import ast.structs.StructMethodCallNode;
import ast.structs.StructUpdateNode;
import tokens.Token;
import ast.variables.AssignmentNode;
import ast.variables.UnaryOpNode;
import ast.variables.VariableNode;
import translate.ListMethodParser;
import translate.Parser;

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
        String tokenVal = parser.current().getValue();

        switch (tokenVal) {
            case "." -> {
                parser.advance();
                String memberName = parser.current().getValue();
                String receiverType = parser.getExpressionType(receiver);

                if (receiverType != null && receiverType.startsWith("Struct")) {

                    StructFieldParser structParser = new StructFieldParser(parser);
                    ASTNode structAccess = structParser.parseAsStatement(receiver, memberName);

                    if (parser.current().getValue().equals("{")) {
                        return parseInlineStructUpdate(structAccess);
                    }

                    if (parser.current().getValue().equals("(")) {
                        List<ASTNode> args = parser.parseArguments();
                        parser.eat(Token.TokenType.DELIMITER, ";");

                        String structName = receiverType.substring("Struct<".length(), receiverType.length() - 1);
                        return new StructMethodCallNode(receiver, structName, memberName, args);
                    }

                    return structAccess;
                }


                // delega para List
                if (receiverType != null && receiverType.startsWith("List")) {
                    ListMethodParser listParser = new ListMethodParser(parser);
                    return listParser.parseStatementListMethod(receiver, memberName);
                }

                // caso seja Struct instanciada diretamente
                if (memberName.equals("Struct")) {
                    parser.advance();
                    String structName = parser.current().getValue();
                    parser.eat(Token.TokenType.IDENTIFIER);

                    String varName = parser.current().getValue();
                    parser.eat(Token.TokenType.IDENTIFIER);

                    StructInstanceParser structParser = new StructInstanceParser(parser);
                    String qualifiedName = name + "." + structName;
                    return structParser.parseStructInstanceAfterKeyword(qualifiedName, varName);
                }

                parser.advance();
                String fullName = name + "." + memberName;
                if (parser.current().getValue().equals("(")) {
                    List<ASTNode> args = parser.parseArguments();
                    parser.eat(Token.TokenType.DELIMITER, ";");
                    return new FunctionCallNode(fullName, args);
                } else {
                    parser.eat(Token.TokenType.DELIMITER, ";");
                    return new FunctionReferenceNode(fullName);
                }
            }

            case "=" -> {
                parser.advance();
                ASTNode value = parser.parseExpression();
                parser.eat(Token.TokenType.DELIMITER, ";");
                return new AssignmentNode(name, value);
            }

            case "++", "--" -> {
                parser.advance();
                parser.eat(Token.TokenType.DELIMITER, ";");
                return new UnaryOpNode(tokenVal, receiver);
            }
        }

        return receiver;
    }

    public ASTNode parseAsExpression(String name) {
        ASTNode receiver = new VariableNode(name);
        Token current = parser.current();

        if (current.getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            return new FunctionCallNode(name, args);
        }

        if (current.getValue().equals(".")) {
            parser.advance();
            String memberName = parser.current().getValue();
            String receiverType = parser.getExpressionType(receiver);

            // delega para Struct
            if (receiverType != null && receiverType.startsWith("Struct")) {
                StructFieldParser structParser = new StructFieldParser(parser);
                return structParser.parseAsExpression(receiver, memberName);
            }

            // delega para List
            if (receiverType != null && receiverType.startsWith("List")) {
                ListMethodParser listParser = new ListMethodParser(parser);
                return listParser.parseExpressionListMethod(receiver, memberName);
            }

            // caso geral
            parser.advance();
            String fullName = name + "." + memberName;
            if (parser.current().getValue().equals("(")) {
                List<ASTNode> args = parser.parseArguments();
                return new FunctionCallNode(fullName, args);
            } else {
                return new FunctionReferenceNode(fullName);
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

            // Campo simples nome: valor;
            if (parser.current().getValue().equals(":")) {
                parser.advance();
                ASTNode value = parser.parseExpression();
                parser.eat(Token.TokenType.DELIMITER, ";");
                fieldUpdates.put(fieldName, value);
                continue;
            }

            // Campo aninhado  nome { ... }
            if (parser.current().getValue().equals("{")) {
                StructUpdateNode nested = parseInlineStructUpdate(
                        new VariableNode(fieldName)
                );
                nestedUpdates.put(fieldName, nested);
                continue;
            }

            throw new RuntimeException("Esperado ':' ou '{' após nome do campo em struct update");
        }

        parser.eat(Token.TokenType.DELIMITER, "}");
        return new StructUpdateNode(target, fieldUpdates, nestedUpdates);
    }
}
