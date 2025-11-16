package ast.maps;

import ast.ASTNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import tokens.Token;
import translate.front.Parser;
import java.util.*;

public class MapParser {
    private final Parser parser;

    public MapParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parse(String varNameFromCaller) {
        String keyType = null;
        String valueType = null;

        // Tipo explícito: Map<int, string>
        if (parser.current().getValue().equals("<")) {
            parser.advance();
            keyType = parser.current().getValue();
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, ",");
            valueType = parser.current().getValue();
            parser.advance();
            parser.eat(Token.TokenType.OPERATOR, ">");
        }

        // Nome da variável
        String varName = varNameFromCaller;
        if (varName == null) {
            varName = parser.current().getValue();
            parser.advance();
        }

        DynamicMap dynamicMap;

        // Inicialização: { "a":1, "b":2 }
        if (parser.current().getValue().equals("=")) {
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, "{");

            Map<ASTNode, ASTNode> entries = new LinkedHashMap<>();

            while (!parser.current().getValue().equals("}")) {
                ASTNode keyNode = parser.parseExpression();
                parser.eat(Token.TokenType.DELIMITER, ":");
                ASTNode valueNode = parser.parseExpression();
                entries.put(keyNode, valueNode);

                if (parser.current().getValue().equals(",")) parser.advance();
            }

            parser.eat(Token.TokenType.DELIMITER, "}");

            // Inferência automática se não tiver tipo explícito
            if (keyType == null || valueType == null) {
                if (entries.isEmpty()) {
                    throw new RuntimeException("Cannot infer type from empty map: " + varName);
                }

                ASTNode firstKey = entries.keySet().iterator().next();
                ASTNode firstValue = entries.values().iterator().next();
                keyType = inferTypeFromNode(firstKey);
                valueType = inferTypeFromNode(firstValue);
            }

            dynamicMap = new DynamicMap(keyType, valueType, entries);

        } else {
            // Mapa vazio precisa de tipo explícito
            if (keyType == null || valueType == null) {
                throw new RuntimeException(
                        "Cannot declare empty Map without explicit type: " + varName
                );
            }
            dynamicMap = new DynamicMap(keyType, valueType, new LinkedHashMap<>());
        }

        parser.eat(Token.TokenType.DELIMITER, ";");

        parser.declareVariableType(varName, "Map<" + keyType + "," + valueType + ">");

        return new VariableDeclarationNode(
                varName,
                "Map<" + keyType + "," + valueType + ">",
                new MapNode(varName, dynamicMap)
        );
    }

    private String inferTypeFromNode(ASTNode node) {
        if (node instanceof LiteralNode lit) {
            return switch (lit.getType()) {
                case "int", "double", "boolean", "string" -> lit.getType();
                default -> throw new RuntimeException("Cannot infer type from literal: " + lit);
            };
        } else if (node instanceof VariableNode varNode) {
            String type = parser.getVariableType(varNode.getName());
            if (type == null) {
                throw new RuntimeException("Cannot infer type from unknown variable: " + varNode.getName());
            }
            return type;
        }
        throw new RuntimeException("Cannot infer type from node: " + node.getClass().getSimpleName());
    }
}
