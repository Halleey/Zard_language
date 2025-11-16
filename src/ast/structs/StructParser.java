package ast.structs;

import ast.variables.VariableDeclarationNode;
import tokens.Token;
import translate.front.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructParser {
    private final Parser parser;

    public StructParser(Parser parser) {
        this.parser = parser;
    }

    public StructNode parseStructAfterKeyword(String structName) {
        parser.eat(Token.TokenType.DELIMITER, "{");

        List<VariableDeclarationNode> fields = new ArrayList<>();
        Map<String, String> fieldTypes = new HashMap<>();

        while (!parser.current().getValue().equals("}")) {
            String fieldType = parseType();

            String fieldName;
            if (parser.current().getType() == Token.TokenType.IDENTIFIER) {
                fieldName = parser.current().getValue();
                parser.eat(Token.TokenType.IDENTIFIER);
            } else {
                if (fieldType.startsWith("Struct ")) {
                    String typeName = fieldType.substring("Struct ".length()).trim();
                    fieldName = Character.toLowerCase(typeName.charAt(0)) + typeName.substring(1);
                } else {
                    throw new RuntimeException("Campo sem nome não suportado: " + fieldType);
                }
            }

            parser.eat(Token.TokenType.DELIMITER, ";");
            fields.add(new VariableDeclarationNode(fieldName, fieldType, null));
            fieldTypes.put(fieldName, fieldType);
        }

        parser.eat(Token.TokenType.DELIMITER, "}");

        // registra no parser
        parser.declareStruct(structName, fieldTypes);

        return new StructNode(structName, fields);
    }

    private String parseType() {
        StringBuilder typeBuilder = new StringBuilder();

        String baseType = parser.current().getValue();
        parser.eat(Token.TokenType.KEYWORD);

        typeBuilder.append(baseType);

        if (baseType.equals("Struct")) {
            String structName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);
            typeBuilder.append("<").append(structName).append(">");
        }

        if (parser.current().getValue().equals("<")) {
            parser.eat(Token.TokenType.OPERATOR, "<");
            typeBuilder.append("<");

            String innerType = parser.current().getValue();
            Token.TokenType innerTypeToken = parser.current().getType();

            if (innerTypeToken == Token.TokenType.KEYWORD) {
                parser.eat(Token.TokenType.KEYWORD);
            } else if (innerTypeToken == Token.TokenType.IDENTIFIER) {
                parser.eat(Token.TokenType.IDENTIFIER);
            }
            else if (innerTypeToken == Token.TokenType.OPERATOR && innerType.equals("?")) {
                parser.eat(Token.TokenType.OPERATOR, "?");
                innerType = "?"; // marcador genérico
            }
            else {
                throw new RuntimeException("Esperado tipo em genérico: " + innerType);
            }

            typeBuilder.append(innerType);

            while (parser.current().getValue().equals(",")) {
                parser.eat(Token.TokenType.DELIMITER, ",");
                typeBuilder.append(",");
                String nextType = parser.current().getValue();
                if (parser.current().getType() == Token.TokenType.KEYWORD) {
                    parser.eat(Token.TokenType.KEYWORD);
                } else if (parser.current().getType() == Token.TokenType.IDENTIFIER) {
                    parser.eat(Token.TokenType.IDENTIFIER);
                } else {
                    throw new RuntimeException("Esperado tipo após vírgula: " + nextType);
                }
                typeBuilder.append(nextType);
            }

            parser.eat(Token.TokenType.OPERATOR, ">");
            typeBuilder.append(">");
        }

        return typeBuilder.toString();
    }
}
