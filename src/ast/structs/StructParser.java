package ast.structs;

import ast.variables.TypeResolver;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
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
        Map<String, Type> fieldTypes = new HashMap<>();

        while (!parser.current().getValue().equals("}")) {

            Type fieldType = parseType();

            String fieldName;

            if (parser.current().getType() == Token.TokenType.IDENTIFIER) {

                fieldName = parser.current().getValue();
                parser.eat(Token.TokenType.IDENTIFIER);

            } else {

                if (fieldType instanceof StructType st) {

                    String typeName = st.name();

                    fieldName = Character.toLowerCase(typeName.charAt(0))
                            + typeName.substring(1);

                } else {
                    throw new RuntimeException(
                            "Campo sem nome não suportado: " + fieldType);
                }
            }

            parser.eat(Token.TokenType.DELIMITER, ";");

            fields.add(new VariableDeclarationNode(fieldName, fieldType, null));

            fieldTypes.put(fieldName, fieldType);
        }

        parser.eat(Token.TokenType.DELIMITER, "}");

        parser.declareStruct(structName, fieldTypes);

        return new StructNode(structName, fields);
    }

    private Type parseType() {
        String baseType = parser.current().getValue();
        parser.advance();

        if (baseType.equals("Struct")) {
            if (parser.current().getType() != Token.TokenType.IDENTIFIER) {
                throw new RuntimeException("Esperado nome do struct após 'Struct'");
            }
            String structName = parser.current().getValue();
            parser.advance();
            baseType = "Struct " + structName; // concatena para TypeResolver
        }

        if (parser.current().getValue().equals("<")) {
            parser.eat(Token.TokenType.OPERATOR, "<");
            String inner = parser.current().getValue();
            parser.advance();
            parser.eat(Token.TokenType.OPERATOR, ">");
            baseType += "<" + inner + ">";
        }

        return TypeResolver.resolve(baseType);
    }
}