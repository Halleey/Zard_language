package ast.structs;

import ast.ASTNode;
import ast.lists.DynamicList;
import ast.lists.ListNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import tokens.Token;
import translate.Parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StructInstanceParser {
    private final Parser parser;

    public StructInstanceParser(Parser parser) {
        this.parser = parser;
    }

    public VariableDeclarationNode parseStructInstanceAfterKeyword(String structName, String varName) {
        List<ASTNode> positionalValues = null;
        Map<String, ASTNode> namedValues = null;

        if (parser.current().getType() == Token.TokenType.OPERATOR &&
                parser.current().getValue().equals("=")) {

            parser.eat(Token.TokenType.OPERATOR, "=");
            parser.eat(Token.TokenType.DELIMITER, "{");

            // Decisão: se próximo token é IDENTIFIER seguido de ":" -> nomeado
            if (parser.current().getType() == Token.TokenType.IDENTIFIER &&
                    parser.peekValue(1).equals(":")) {
                namedValues = parseNamedInitializers(structName);
            } else {
                positionalValues = parsePositionalInitializers();
            }

            parser.eat(Token.TokenType.DELIMITER, "}");
        }

        parser.eat(Token.TokenType.DELIMITER, ";");

        StructInstaceNode instanceNode = new StructInstaceNode(structName, positionalValues, namedValues);

        parser.declareVariable(varName, "Struct<" + structName + ">");
        return new VariableDeclarationNode(varName, "Struct<" + structName + ">", instanceNode);
    }

    public VariableDeclarationNode parseStructInline(String structName, String varName) {
        parser.eat(Token.TokenType.DELIMITER, "{");

        List<ASTNode> positionalValues = null;
        Map<String, ASTNode> namedValues = null;

        if (parser.current().getType() == Token.TokenType.IDENTIFIER &&
                parser.peekValue(1).equals(":")) {
            namedValues = parseNamedInitializers(structName);
        } else {
            positionalValues = parsePositionalInitializers();
        }

        parser.eat(Token.TokenType.DELIMITER, "}");
        parser.eat(Token.TokenType.DELIMITER, ";");

        StructInstaceNode instanceNode = new StructInstaceNode(structName, positionalValues, namedValues);
        parser.declareVariable(varName, "Struct<" + structName + ">");
        return new VariableDeclarationNode(varName, "Struct<" + structName + ">", instanceNode);
    }

    private List<ASTNode> parsePositionalInitializers() {
        List<ASTNode> values = new ArrayList<>();
        while (!parser.current().getValue().equals("}")) {
            ASTNode value = parser.parseExpression();
            values.add(value);

            if (parser.current().getValue().equals(",")) {
                parser.eat(Token.TokenType.DELIMITER, ",");
            } else {
                break;
            }
        }
        return values;
    }

    private Map<String, ASTNode> parseNamedInitializers(String structName) {
        Map<String, ASTNode> map = new LinkedHashMap<>();
        Map<String, String> fields = parser.lookupStruct(structName);

        while (!parser.current().getValue().equals("}")) {
            String fieldName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);
            parser.eat(Token.TokenType.DELIMITER, ":");

            String expectedType = fields.get(fieldName);
            if (expectedType == null) {
                throw new RuntimeException("Campo desconhecido na inicialização de " + structName + ": " + fieldName);
            }

            ASTNode expr;

            if (expectedType.startsWith("List<")) {
                List<ASTNode> listValues = new ArrayList<>();
                listValues.add(parser.parseExpression());

                while (parser.current().getValue().equals(",")) {
                    parser.eat(Token.TokenType.DELIMITER, ",");
                    if (parser.current().getType() == Token.TokenType.IDENTIFIER &&
                            parser.peekValue(1).equals(":")) {
                        break;
                    }
                    listValues.add(parser.parseExpression());
                }

                String innerType = expectedType.substring(5, expectedType.length() - 1);
                if (innerType.equals("?")) {
                    innerType = inferListTypeFromValues(listValues); // chama o método abaixo
                    expectedType = "List<" + innerType + ">";
                }

                DynamicList dyn = new DynamicList(innerType, listValues);
                expr = new ListNode(dyn);
            }


            else if (isPrimitiveType(expectedType)) {
                expr = parser.parseExpression();
            }

            else {
                throw new RuntimeException("Inicialização nomeada ainda não suporta tipo não-primitivo para o campo '"
                        + fieldName + "' (tipo: " + expectedType + ").");
            }

            if (map.containsKey(fieldName)) {
                throw new RuntimeException("Campo duplicado na inicialização: " + fieldName);
            }

            map.put(fieldName, expr);

            // consome vírgula final entre pares
            if (parser.current().getValue().equals(",")) {
                parser.eat(Token.TokenType.DELIMITER, ",");
            } else {
                break;
            }
        }

        return map;
    }

    private boolean isPrimitiveType(String t) {
        String s = t.trim().toLowerCase();
        return s.equals("int") || s.equals("double") || s.equals("bool") ||
                s.equals("string") || s.equals("%string") || s.equals("String");
    }

    private String inferListTypeFromValues(List<ASTNode> values) {
        if (values.isEmpty()) return "any";

        ASTNode first = values.get(0);

        // Literais básicos
        if (first instanceof LiteralNode literal) {
            String litType = literal.getValue().type();
            switch (litType) {
                case "int": return "int";
                case "double": return "double";
                case "float": return "float";
                case "string": return "string";
                case "boolean": return "boolean";
            }
        }
        // Structs
        if (first instanceof ast.structs.StructInstaceNode structNode) {
            return "Struct<" + structNode.getName() + ">";
        }

        return "any"; // fallback
    }


}