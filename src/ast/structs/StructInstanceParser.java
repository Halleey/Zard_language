package ast.structs;

import ast.ASTNode;
import ast.lists.DynamicList;
import ast.lists.ListNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
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

        String innerType = parseOptionalInnerType(); // <int> ou null

        List<ASTNode> positionalValues = null;
        Map<String, ASTNode> namedValues = null;

        // Detecta inicialização com ou sem '='
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

        // Monta tipo final "Struct<Set<int>>"
        String variableType = buildStructType(structName, innerType);

        StructInstaceNode instanceNode =
                new StructInstaceNode(structName, positionalValues, namedValues);
        instanceNode.setConcreteType(variableType);

        parser.declareVariable(varName, variableType);
        return new VariableDeclarationNode(varName, variableType, instanceNode);
    }

    private String parseOptionalInnerType() {
        if (accept("<")) {
            String type = parser.current().getValue();
            parser.advance();
            parser.eat(Token.TokenType.OPERATOR, ">");
            return type;
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

    private String buildStructType(String structName, String innerType) {
        if (innerType != null) structName += "<" + innerType + ">";
        return "Struct<" + structName + ">";
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
        Map<String, String> fields = parser.lookupStruct(structName);

        while (!parser.current().getValue().equals("}")) {
            String fieldName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);
            parser.eat(Token.TokenType.DELIMITER, ":");

            String expectedType = fields.get(fieldName);
            if (expectedType == null)
                throw new RuntimeException("Campo desconhecido em " + structName + ": " + fieldName);

            ASTNode expr = parseInitializerValue(expectedType);

            if (result.containsKey(fieldName))
                throw new RuntimeException("Campo duplicado: " + fieldName);

            result.put(fieldName, expr);
            if (!accept(",")) break;
        }

        return result;
    }

    private ASTNode parseInitializerValue(String expectedType) {

        if (expectedType.startsWith("List<")) {
            List<ASTNode> listValues = new ArrayList<>();
            listValues.add(parser.parseExpression());

            while (accept(",")) {
                if (parser.current().getType() == Token.TokenType.IDENTIFIER &&
                        parser.peekValue(1).equals(":")) break;
                listValues.add(parser.parseExpression());
            }

            String innerType = expectedType.substring(5, expectedType.length() - 1);
            if (innerType.equals("?"))
                innerType = inferListTypeFromValues(listValues);

            DynamicList dyn = new DynamicList(innerType, listValues);
            return new ListNode(dyn);
        }

        // Primitivos ou Struct
        return parser.parseExpression();
    }

    private String inferListTypeFromValues(List<ASTNode> values) {
        if (values.isEmpty()) return "any";
        ASTNode v = values.get(0);

        if (v instanceof LiteralNode lit) return lit.getValue().type();
        if (v instanceof StructInstaceNode s) return "Struct<" + s.getName() + ">";

        return "any";
    }
}
