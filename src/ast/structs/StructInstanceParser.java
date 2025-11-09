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


import java.util.*;

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

            if (parser.current().getType() == Token.TokenType.IDENTIFIER &&
                    parser.peekValue(1).equals(":")) {
                namedValues = parseNamedInitializers(structName);
            } else {
                positionalValues = parsePositionalInitializers();
            }

            parser.eat(Token.TokenType.DELIMITER, "}");
            parser.eat(Token.TokenType.DELIMITER, ";");
        }

        else if (parser.current().getValue().equals("{")) {
            parser.eat(Token.TokenType.DELIMITER, "{");

            if (parser.current().getType() == Token.TokenType.IDENTIFIER &&
                    parser.peekValue(1).equals(":")) {
                namedValues = parseNamedInitializers(structName);
            } else {
                positionalValues = parsePositionalInitializers();
            }

            parser.eat(Token.TokenType.DELIMITER, "}");
            parser.eat(Token.TokenType.DELIMITER, ";");
        }

        else {
            parser.eat(Token.TokenType.DELIMITER, ";");
        }

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
            } else break;
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
            if (expectedType == null)
                throw new RuntimeException("Campo desconhecido em " + structName + ": " + fieldName);

            ASTNode expr;

            if (expectedType.startsWith("List<")) {
                List<ASTNode> listValues = new ArrayList<>();
                listValues.add(parser.parseExpression());

                while (parser.current().getValue().equals(",")) {
                    parser.eat(Token.TokenType.DELIMITER, ",");
                    if (parser.current().getType() == Token.TokenType.IDENTIFIER &&
                            parser.peekValue(1).equals(":"))
                        break;
                    listValues.add(parser.parseExpression());
                }

                String innerType = expectedType.substring(5, expectedType.length() - 1);
                if (innerType.equals("?")) {
                    innerType = inferListTypeFromValues(listValues);
                    expectedType = "List<" + innerType + ">";
                }

                DynamicList dyn = new DynamicList(innerType, listValues);
                expr = new ListNode(dyn);
            } else if (isPrimitiveType(expectedType)) {
                expr = parser.parseExpression();
            } else {
                throw new RuntimeException("Tipo não suportado para campo '" + fieldName + "' em struct " + structName);
            }

            if (map.containsKey(fieldName))
                throw new RuntimeException("Campo duplicado: " + fieldName);

            map.put(fieldName, expr);

            if (parser.current().getValue().equals(",")) parser.eat(Token.TokenType.DELIMITER, ",");
            else break;
        }

        return map;
    }

    private boolean isPrimitiveType(String t) {
        String s = t.trim().toLowerCase();
        return s.equals("int") || s.equals("double") || s.equals("float") ||
                s.equals("bool") || s.equals("boolean") ||
                s.equals("string") || s.equals("%string");
    }

    private String inferListTypeFromValues(List<ASTNode> values) {
        if (values.isEmpty()) return "any";
        ASTNode first = values.get(0);

        if (first instanceof LiteralNode literal) {
            switch (literal.getValue().type()) {
                case "int": return "int";
                case "double": return "double";
                case "float": return "float";
                case "string": return "string";
                case "boolean": return "boolean";
            }
        }
        if (first instanceof StructInstaceNode s)
            return "Struct<" + s.getName() + ">";

        return "any";
    }
}
