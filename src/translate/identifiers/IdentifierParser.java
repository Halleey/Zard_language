package translate.identifiers;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionReferenceNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructInstanceParser;
import ast.structs.StructMethodCallNode;
import ast.structs.StructUpdateNode;
import ast.variables.VariableDeclarationNode;
import helpers_ast.variables.AssignmentParser;
import helpers_ast.variables.UnaryParser;
import tokens.Token;
import ast.variables.VariableNode;
import translate.front.Parser;

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

            String structName = parseTypeStruct(name);

        if (parser.isKnownStruct(name)) {
            String varName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);

            // Caso especial: inicialização com { }
            if (parser.current().getValue().equals("=")) {
                parser.eat(Token.TokenType.OPERATOR, "=");

                if (parser.current().getValue().equals("{")) {
                    StructInstanceParser structParser = new StructInstanceParser(parser);
                    return structParser.parseStructInstanceAfterKeyword(structName, varName);
                }

                ASTNode initializer = parser.parseExpression();
                parser.eat(Token.TokenType.DELIMITER, ";");
                parser.declareVariable(varName, "Struct<" + structName + ">");

                return new VariableDeclarationNode(varName, "Struct<" + structName + ">", initializer);
            }

            // Instanciação direta com chaves:
            if (parser.current().getValue().equals("{")) {
                StructInstanceParser structParser = new StructInstanceParser(parser);
                return structParser.parseStructInstanceAfterKeyword(structName, varName);
            }

            // Declaração simples:
            parser.eat(Token.TokenType.DELIMITER, ";");
            StructInstaceNode instanceNode = new StructInstaceNode(structName, null, null);

            parser.declareVariable(varName, "Struct<" + structName + ">");
            return new VariableDeclarationNode(varName, "Struct<" + structName + ">", instanceNode);
        }

        if (parser.current().getType() == Token.TokenType.IDENTIFIER) {
            String varName = parser.current().getValue();
            Token afterVar = parser.peek(1);

            if (afterVar.getType() == Token.TokenType.DELIMITER &&
                    afterVar.getValue().equals(";")) {

                parser.eat(Token.TokenType.IDENTIFIER);
                parser.eat(Token.TokenType.DELIMITER, ";");

                String finalType = "Struct<" + structName + ">";
                parser.declareVariable(varName, finalType);

                return new VariableDeclarationNode(varName, finalType, null);
            }
        }
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
                        String structType = receiverType.substring("Struct<".length(), receiverType.length() - 1);
                        return new StructMethodCallNode(receiver, structType, memberName, args);
                    }

                    return structAccess;
                }

                if (receiverType != null && receiverType.startsWith("List")) {
                    ListMethodParser listParser = new ListMethodParser(parser);
                    return listParser.parseStatementListMethod(receiver, memberName);
                }

                parser.advance();
                String fullName = name + "." + memberName;

                if (parser.current().getValue().equals("(")) {
                    List<ASTNode> args = parser.parseArguments();
                    if (parser.current().getValue().equals(";")) parser.eat(Token.TokenType.DELIMITER, ";");
                    return new FunctionCallNode(fullName, args);
                } else {
                    if (parser.current().getValue().equals(";")) parser.eat(Token.TokenType.DELIMITER, ";");
                    return new VariableNode(fullName);
                }
            }

            case "=" -> {
                return new AssignmentParser(parser).parse(name);
            }

            case "++", "--" -> {
                return new UnaryParser(parser).parser(name, tokenVal);
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
                StructUpdateNode nested = parseInlineStructUpdate(new VariableNode(fieldName));
                nestedUpdates.put(fieldName, nested);
                continue;
            }

            throw new RuntimeException("Esperado ':' ou '{' após nome do campo em struct update");
        }

        parser.eat(Token.TokenType.DELIMITER, "}");
        return new StructUpdateNode(target, fieldUpdates, nestedUpdates);
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

            if (receiverType != null && receiverType.startsWith("Struct")) {
                StructFieldParser structParser = new StructFieldParser(parser);
                return structParser.parseAsExpression(receiver, memberName);
            }

            if (receiverType != null && receiverType.startsWith("List")) {
                ListMethodParser listParser = new ListMethodParser(parser);
                return listParser.parseExpressionListMethod(receiver, memberName);
            }

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
        private String parseTypeStruct(String baseName) {

            if (!parser.current().getValue().equals("<")) {
                return baseName;
            }

            parser.advance();

            Token typeToken = parser.current();

            if (typeToken.getType() != Token.TokenType.IDENTIFIER &&
                    typeToken.getType() != Token.TokenType.KEYWORD) {

                throw new RuntimeException(
                        "Tipo inválido em especialização de struct: " + typeToken
                );
            }

            String innerType = typeToken.getValue();
            parser.advance();

            parser.eat(Token.TokenType.OPERATOR, ">");

            return baseName + "<" + innerType + ">";
        }

    }