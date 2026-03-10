package ast.structs;

import ast.ASTNode;
import ast.lists.DynamicList;
import ast.lists.ListNode;
import ast.variables.LiteralNode;
import ast.variables.TypeResolver;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.ListType;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import tokens.Token;
import translate.front.Parser;

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

        System.out.println("[StructInstanceParser] Começando para " + varName + " do tipo " + structName);

        Type innerType = parseOptionalInnerType();

        List<ASTNode> positionalValues = null;
        Map<String, ASTNode> namedValues = null;

        if (accept("=")) {
            parser.eat(Token.TokenType.DELIMITER, "{");

            namedValues = tryParseNamed(structName);
            if (namedValues == null)
                positionalValues = parsePositionalInitializers();

            parser.eat(Token.TokenType.DELIMITER, "}");
            parser.eat(Token.TokenType.DELIMITER, ";");
        }
        else if (accept("{")) {
            namedValues = tryParseNamed(structName);
            if (namedValues == null)
                positionalValues = parsePositionalInitializers();

            parser.eat(Token.TokenType.DELIMITER, "}");
            parser.eat(Token.TokenType.DELIMITER, ";");
        }
        else {
            parser.eat(Token.TokenType.DELIMITER, ";");
        }

        Type variableType = buildStructType(structName, innerType);

        StructInstanceNode instanceNode =
                new StructInstanceNode(structName, positionalValues, namedValues);

        instanceNode.setResolvedType(variableType);

        System.out.println("[StructInstanceParser] Declarando variável " + varName + " com tipo " + variableType);

        parser.declareVariable(varName, variableType);

        return new VariableDeclarationNode(varName, variableType, instanceNode);
    }

    private Type parseOptionalInnerType() {

        if (accept("<")) {

            String typeName = parser.current().getValue();
            parser.advance();

            parser.eat(Token.TokenType.OPERATOR, ">");

            return TypeResolver.resolve(typeName);
        }

        return null;
    }

    private boolean accept(String value) {
        if (parser.current().getValue().equals(value)) {
            parser.advance();
            return true;
        }
        return false;
    }

    private Type buildStructType(String structName, Type innerType) {

        if (innerType != null) {
            return new StructType(structName + "<" + innerType + ">");
        }

        return new StructType(structName);
    }

    private Map<String, ASTNode> tryParseNamed(String structName) {

        if (parser.current().getType() == Token.TokenType.IDENTIFIER &&
                parser.peekValue(1).equals(":")) {

            return parseNamedInitializers(stripGeneric(structName));
        }

        return null;
    }

    private List<ASTNode> parsePositionalInitializers() {

        List<ASTNode> values = new ArrayList<>();

        while (!parser.current().getValue().equals("}")) {

            values.add(parser.parseExpression());

            if (!accept(",")) break;
        }

        return values;
    }

    private String stripGeneric(String name) {

        int idx = name.indexOf('<');
        return (idx != -1) ? name.substring(0, idx) : name;
    }

    private Map<String, ASTNode> parseNamedInitializers(String structName) {

        Map<String, ASTNode> result = new LinkedHashMap<>();

        Map<String, Type> fields = parser.lookupStruct(structName);

        while (!parser.current().getValue().equals("}")) {

            String fieldName = parser.current().getValue();

            parser.eat(Token.TokenType.IDENTIFIER);
            parser.eat(Token.TokenType.DELIMITER, ":");

            Type expectedType = fields.get(fieldName);

            if (expectedType == null)
                throw new RuntimeException(
                        "Campo desconhecido em " + structName + ": " + fieldName);

            ASTNode expr = parseInitializerValue(expectedType);

            if (result.containsKey(fieldName))
                throw new RuntimeException("Campo duplicado: " + fieldName);

            result.put(fieldName, expr);

            if (!accept(",")) break;
        }

        return result;
    }

    private ASTNode parseInitializerValue(Type expectedType) {

        if (expectedType instanceof ListType listType) {

            List<ASTNode> listValues = new ArrayList<>();

            listValues.add(parser.parseExpression());

            while (accept(",")) {

                if (parser.current().getType() == Token.TokenType.IDENTIFIER &&
                        parser.peekValue(1).equals(":"))
                    break;

                listValues.add(parser.parseExpression());
            }

            boolean isReference = false;

            Type innerType = listType.elementType();

            DynamicList dyn = new DynamicList(innerType, listValues, isReference);

            return new ListNode(dyn);
        }

        return parser.parseExpression();
    }
}